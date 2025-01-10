package com.example.playcation.order.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.repository.OrderDetailRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

  private final OrderDetailRepository orderDetailRepository;
  private final GameRepository gameRepository;

  /**
   * 장바구니 내 아이템들로 OrderDetail 생성(아직 주문 부여X). 유효하지 않은 값을 포함할 시 예외
   *
   * @param items 장바구니 아이템 리스트
   * @return {@code items}를 OrderDetail 로 변환한 리스트
   */
  @Transactional
  public List<OrderDetail> createOrderDetailsInCart(List<CartGameResponseDto> items) {

    List<Long> ids = items.stream().map(CartGameResponseDto::getId).toList();
    List<Game> games = gameRepository.findAllByIdIn(ids);
    // TODO: 게임 유효성 검증
    checkGamesValid(games);

    List<OrderDetail> orderDetails = new ArrayList<>();
    for (int i = 0; i < items.size(); i++) {
      orderDetails.add(
          OrderDetail.builder()
              .game(games.get(i))
              .price(items.get(i).getPrice())
              .build()
      );
    }

    return orderDetailRepository.saveAll(orderDetails);
  }

  /**
   * OrderDetail 에 Order 부여
   *
   * @param orderDetails OrderDetail 목록
   * @param order 부여할 Order
   */
  @Transactional
  public void assignOrder(List<OrderDetail> orderDetails, Order order) {

    for(OrderDetail o : orderDetails) {
      o.assignOrder(order);
    }
  }

  /**
   * 게임 목록의 유효성 검사. 삭제된 게임이 있을시 예외
   *
   * @param games 게임 목록
   */
  private void checkGamesValid(List<Game> games) {
//    List<Long> invalidIds = new ArrayList<>();
//
//    boolean isInvalid = false;
//    for (Game g : games) {
//      if (g.getDeletedAt() != null) {
//        isInvalid = true;
//        invalidIds.add(g.getId());
//      }
//    }
//    if (isInvalid) {
//      throw new InvalidInputException(OrderErrorCode.INVALID_ITEM_INCLUDED);
//    }
  }
}
