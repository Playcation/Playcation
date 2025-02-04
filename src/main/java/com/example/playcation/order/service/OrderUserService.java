package com.example.playcation.order.service;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.common.PagingDto;
import com.example.playcation.emailsender.service.OrderEmailService;
import com.example.playcation.enums.OrderStatus;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.OrderErrorCode;
import com.example.playcation.game.dto.GameSimpleResponseDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.library.service.LibraryService;
import com.example.playcation.order.dto.OrderDetailResponseDto;
import com.example.playcation.order.dto.OrderProceedResponseDto;
import com.example.playcation.order.dto.OrderResponseDto;
import com.example.playcation.order.dto.RefundRequestDto;
import com.example.playcation.order.dto.RefundResponseDto;
import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.entity.Refund;
import com.example.playcation.order.repository.OrderDetailRepository;
import com.example.playcation.order.repository.OrderRepository;
import com.example.playcation.order.repository.RefundRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.user.service.UserService;
import jakarta.mail.MessagingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderUserService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final OrderDetailRepository orderDetailRepository;
  private final RefundRepository refundRepository;
  private final GameRepository gameRepository;

  private final CartService cartService;
  private final OrderDetailService orderDetailService;
  private final LibraryService libraryService;
  private final UserService userService;

  private final OrderEmailService orderEmailService;

  /**
   * 결제를 위한 주문 정보 생성
   *
   * @param userId 현재 로그인한 유저 id
   */
  @Transactional
  public OrderProceedResponseDto createOrder(Long userId) {

    List<CartGameResponseDto> cartItems = cartService.findCartItems(userId);
    if (cartItems.isEmpty()) {
      throw new InvalidInputException(OrderErrorCode.EMPTY_CART);
    }

    // 장바구니 내 게임 유효성 검사
    List<Long> ids = cartItems.stream().map(CartGameResponseDto::getId).toList();
    List<Game> games = gameRepository.findAllByIdIn(ids);
    for (Game g : games) {
      if(g.isDeleted()) {
        throw new InvalidInputException(OrderErrorCode.INVALID_ITEM_INCLUDED);
      }
    }

    BigDecimal total = cartItems.stream().map(CartGameResponseDto::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    User findUser = userRepository.findByIdOrElseThrow(userId);

    Order order = Order.builder()
        .user(findUser)
        .totalPrice(total)
        .status(OrderStatus.IN_PROGRESS)
        .build();
    Order savedOrder = orderRepository.save(order);
    orderDetailService.createOrderDetailsInCart(cartItems, savedOrder);

    return new OrderProceedResponseDto(savedOrder.getId().toString(), cartItems, total);
  }

  /**
   * 결제 완료된 주문의 정보를 저장
   */
  public OrderResponseDto completeOrder(Long userId, String orderId) {

    cartService.removeCart(userId);

    List<OrderDetail> details = orderDetailRepository.findAllByOrderId(UUID.fromString(orderId));
    Order order = details.get(0).getOrder();

    List<Long> gameIds = details.stream().map(d -> d.getGame().getId()).toList();
    libraryService.createLibraries(gameIds, order.getUser());
    order.successStatus();

    try {
      // 이메일 발송(주문, 주문 상세내역)
      orderEmailService.sendOrderConfirmationEmail(order, details);
    } catch (MessagingException e) {
      // 예외 로그
      log.error("주문 확인 이메일 전송에 실패했습니다. 주문 ID: {}", order.getId(), e);
    }

    return OrderResponseDto.toDto(order, details);
  }

  /**
   * 주문 단건 조회. 권한 부족시 예외
   *
   * @param userId  현재 로그인한 유저 id
   * @param orderId 조회할 주문 식별자
   */
  public OrderResponseDto findOrder(Long userId, UUID orderId) {

    Order findOrder = orderRepository.findByIdOrElseThrow(orderId);
    User findUser = userRepository.findByIdOrElseThrow(userId);

    if (findOrder.getUser() != findUser) {
      throw new NoAuthorizedException(OrderErrorCode.NO_AUTHORIZED_ORDER);
    }

    List<OrderDetail> details = orderDetailRepository.findAllByOrderId(orderId);

    return OrderResponseDto.toDto(findOrder, details);
  }

  /**
   * 주문 다건 조회 페이징(최대 10건까지)
   *
   * @param page   조회할 페이지 번호
   * @param userId 현재 로그인한 유저 식별자
   */
  public PagingDto<OrderResponseDto> findAllOrders(int page, int size, Long userId) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdAt"));

    Page<Order> orders = orderRepository.findAllByUserId(userId, pageable);

    List<OrderDetail> details = orderDetailRepository.findAllByOrderIn(orders.getContent());
    List<OrderResponseDto> dtos = new ArrayList<>();

    for (Order o : orders) {
      dtos.add(
          OrderResponseDto.toDto(o, details.stream().filter(d -> d.getOrder().equals(o)).toList()));
    }

    return new PagingDto<>(dtos, orders.getTotalElements());
  }

  /**
   * 환불 요청
   *
   * @apiNote 주문일로부터 2일 이내인 주문만 환불 가능, 비밀번호 체크.<br>주문 내역 페이지에서 환불 진행.
   */
  @Transactional
  public RefundResponseDto refundOrder(Long userId, UUID orderId, RefundRequestDto dto) {

    Order findOrder = orderRepository.findByIdOrElseThrow(orderId);
    if (!orderRepository.existsByIdAndUserId(orderId, userId)) {
      throw new InvalidInputException(OrderErrorCode.NO_AUTHORIZED_ORDER);
    }
    if (!orderDetailRepository.existsByIdAndOrderId(dto.getOrderDetailId(), orderId)) {
      throw new InvalidInputException(OrderErrorCode.NO_EXIST_ORDER_DETAIL);
    }
    if (findOrder.getCreatedAt().isBefore(LocalDateTime.now().minusDays(2))) {
      throw new InvalidInputException(OrderErrorCode.REFUND_PERIOD_EXPIRED);
    }

    User findUser = userRepository.findByIdOrElseThrow(userId);
    userService.checkPassword(findUser, dto.getPassword());

    OrderDetail findOrderDetail = orderDetailRepository.findByIdOrElseThrow(dto.getOrderDetailId());
    Refund refund = Refund.builder()
        .orderDetail(findOrderDetail)
        .refundMessage(dto.getRefundMessage())
        .build();

    Refund savedRefund = refundRepository.save(refund);
    findOrderDetail.updateStatus(OrderStatus.EXPIRED);
    findOrderDetail.updateRefund(savedRefund);

    // ************************
    // TODO: 환불 진행
    // ************************

    return RefundResponseDto.toDto(savedRefund, OrderStatus.EXPIRED);
  }

  /**
   * 가장 최근의 주문 한 건을 검색
   *
   * @param userId 현재 로그인한 유저
   * @return 가장 최근 주문의 게임 목록
   */
  public List<GameSimpleResponseDto> findLatestOrder(Long userId) {

    Order latestOrder = orderRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    List<OrderDetail> findDetail = orderDetailRepository.findAllByOrderId(latestOrder.getId());

    return findDetail.stream().map(GameSimpleResponseDto::orderDetailToDto).toList();
  }
}
