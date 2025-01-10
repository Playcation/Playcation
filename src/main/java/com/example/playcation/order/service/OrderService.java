package com.example.playcation.order.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.enums.OrderStatus;
import com.example.playcation.enums.Role;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.OrderErrorCode;
import com.example.playcation.game.entity.Game;
import com.example.playcation.order.dto.OrderResponseDto;
import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.repository.OrderRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;

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

    // 총액(total) 계산
    BigDecimal total = calculateTotalPrice(details, BigDecimal.ZERO, 0);

    // TODO: 결제 요청
    OrderStatus status = OrderStatus.SUCCESS;

    Order order = Order.builder()
        .user(findUser)
        .totalPrice(total)
        .status(status)
        .build();
    Order savedOrder = orderRepository.save(order);

    orderDetailService.assignOrder(details, savedOrder);

    return OrderResponseDto.toDto(order, cartItems);
  }

  /**
   * 주문 단건 조회. 권한 부족시 예외
   *
   * @param userId 현재 로그인한 유저 id
   * @param orderId 조회할 주문 식별자
   * @param auth 현재 로그인한 유저 권한
   */
  public OrderResponseDto findOrder(Long userId, Long orderId, String auth) {

    Order findOrder = orderRepository.findByIdOrElseThrow(orderId);
    User findUser = userRepository.findByIdOrElseThrow(userId);
    List<Game> games = orderDetailService.findOrderedGames(orderId);

    boolean authFlag = true;
    if (auth.equals(Role.MANAGER.name())) {
      if (!findUser.isManagerOfGames(games)) {
        // 유저가 MANAGER 이지만 주문 내역에서 소유한 게임이 없을시
        authFlag = false;
      }
    } else if (!findOrder.getUser().equals(findUser)) {
      // 유저가 USER 이면서 주문의 소유자가 아닐시
      authFlag = false;
    }

    if (authFlag) {
      return OrderResponseDto.toDto(findOrder,
          games.stream().map(CartGameResponseDto::toDto).toList());
    } else {
      throw new NoAuthorizedException(OrderErrorCode.NO_AUTHORIZED_ORDER);
    }
  }

  /**
   * OrderDetail 리스트의 총액을 계산하는 재귀 메서드
   *
   * @param details OrderDetail 리스트
   * @param total   계산하는 총액
   * @param count   종료 기준
   * @return 주문 총액
   */
  private BigDecimal calculateTotalPrice(List<OrderDetail> details, BigDecimal total, int count) {

    if (count >= details.size()) {
      return total;
    }
    return calculateTotalPrice(details, total.add(details.get(count++).getPrice()), count);
  }
}
