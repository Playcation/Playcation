package com.example.playcation.order.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.enums.OrderStatus;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.OrderErrorCode;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.order.dto.OrderResponseDto;
import com.example.playcation.order.entity.Order;
import com.example.playcation.order.repository.OrderRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
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
  private final GameRepository gameRepository;

  private final CartService cartService;

  /**
   * 주문 생성(결제)
   *
   * @param userId
   * @return
   */
  @Transactional
  public OrderResponseDto createOrder(Long userId) {

    List<CartGameResponseDto> cartItems = cartService.findCartItems(userId);
    User findUser = userRepository.findByIdOrElseThrow(userId);

    // TODO: 게임 삭제 방식 결정 -> 장바구니 아이템 유효성 검사
    // checkItemsValid(cartItems);

    // 총액(total) 계산
    List<BigDecimal> priceList = cartItems.stream().map(CartGameResponseDto::getPrice).toList();
    BigDecimal total = calculateTotalPrice(priceList, priceList.get(0), 0);

    // TODO: 결제 요청
    OrderStatus status = OrderStatus.SUCCESS;

    Order order = Order.builder()
        .user(findUser)
        .totalPrice(total)
        .status(status)
        .build();
    orderRepository.save(order);

    return OrderResponseDto.toDto(order, cartItems);
  }

  /**
   * 장바구니의 게임이 삭제되었는지를 확인
   *
   * @param items 장바구니 게임 리스트
   */
  /*private void checkItemsValid(List<CartGameResponseDto> items) {
    List<Long> ids = items.stream().map(CartGameResponseDto::getId).toList();
    List<Game> games = gameRepository.findAllByIdIn(ids);
    // TODO: 예외 전달 방식
    //  어떤 게임에서 예외가 발생했는지도 함께 출력하기?
    List<Long> invalidIds = new ArrayList<>();

    boolean isInvalid = false;
    for (Game g : games) {
      if (g.getDeletedAt() != null) {
        isInvalid = true;
        invalidIds.add(g.getId());
      }
    }
    if (isInvalid) {
      throw new InvalidInputException(OrderErrorCode.INVALID_ITEM_INCLUDED);
    }
  }*/

  /**
   * 게임 가격 리스트의 총액을 계산하는 재귀 메서드
   *
   * @param priceList 가격 리스트
   * @param price 가격
   * @param count 종료 기준
   * @return 가격 리스트 총액
   */
  private BigDecimal calculateTotalPrice(List<BigDecimal> priceList, BigDecimal price, int count) {
    count += 1;
    if(count >= priceList.size()) {
      return price;
    }
    log.info(price.toString());
    return calculateTotalPrice(priceList, price.add(priceList.get(count)), count);
  }
}
