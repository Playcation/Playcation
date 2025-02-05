package com.example.playcation.order.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.enums.OrderStatus;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.library.service.LibraryService;
import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.repository.OrderDetailRepository;
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
   * 장바구니 내 아이템들로 OrderDetail 생성(아직 주문 부여X)
   *
   * @param items 장바구니 아이템 리스트
   * @param order 주문 번호
   */
  @Transactional
  public void createOrderDetailsInCart(List<CartGameResponseDto> items, Order order) {

    List<Long> ids = items.stream().map(CartGameResponseDto::getId).toList();
    List<Game> games = gameRepository.findAllByIdIn(ids);

    List<OrderDetail> orderDetails = new ArrayList<>();
    for (int i = 0; i < items.size(); i++) {
      orderDetails.add(
          OrderDetail.builder()
              .order(order)
              .game(games.get(i))
              .price(items.get(i).getPrice())
              .status(OrderStatus.SUCCESS)
              .build()
      );
    }

    orderDetailRepository.saveAll(orderDetails);
  }
}
