package com.example.playcation.order.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.enums.OrderStatus;
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
   * OrderDetail 리스트의 총액을 계산하는 재귀 메서드
   *
   * @param details   OrderDetail 리스트
   * @param total     계산하는 총액
   * @param count     종료 기준
   * @return 주문 총액
   */
  private BigDecimal calculateTotalPrice(List<OrderDetail> details, BigDecimal total, int count) {

    if (count >= details.size()) {
      return total;
    }
    return calculateTotalPrice(details, total.add(details.get(count++).getPrice()), count);
  }
}
