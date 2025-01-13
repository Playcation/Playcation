package com.example.playcation.order.service;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.OrderStatus;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.OrderErrorCode;
import com.example.playcation.order.dto.OrderResponseDto;
import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.repository.OrderDetailRepository;
import com.example.playcation.order.repository.OrderRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

  private final CartService cartService;
  private final OrderDetailService orderDetailService;

  /**
   * 주문 생성(결제)
   *
   * @param userId 현재 로그인한 유저 id
   */
  @Transactional
  public OrderResponseDto createOrder(Long userId) {

    List<CartGameResponseDto> cartItems = cartService.findCartItems(userId);
    User findUser = userRepository.findByIdOrElseThrow(userId);

    // 주문 상세 내역 생성 및 장바구니 내 게임 유효성 검사
    List<OrderDetail> details = orderDetailService.createOrderDetailsInCart(cartItems);
    BigDecimal total = details.stream().map(OrderDetail::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // ************************
    // TODO: 결제 요청
    // ************************

    OrderStatus status = OrderStatus.SUCCESS;

    Order order = Order.builder()
        .user(findUser)
        .totalPrice(total)
        .status(status)
        .build();

    Order savedOrder = orderRepository.save(order);
    for (OrderDetail o : details) {
      o.assignOrder(savedOrder);
    }

    return OrderResponseDto.toDto(savedOrder, details);
  }

  /**
   * 주문 단건 조회. 권한 부족시 예외
   *
   * @param userId  현재 로그인한 유저 id
   * @param orderId 조회할 주문 식별자
   */
  public OrderResponseDto findOrder(Long userId, Long orderId) {

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
}
