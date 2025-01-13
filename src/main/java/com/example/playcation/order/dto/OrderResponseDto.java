package com.example.playcation.order.dto;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.enums.OrderStatus;
import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderResponseDto {

  private final long id;

  private final List<CartGameResponseDto> games;

  private final BigDecimal totalPrice;

  private final LocalDateTime orderedAt;

  private final OrderStatus status;

  public static OrderResponseDto toDto(Order order, List<OrderDetail> orderDetails) {

    List<CartGameResponseDto> games = orderDetails.stream()
        .map(orderDetail -> CartGameResponseDto.toDto(orderDetail.getGame())).toList();

    return new OrderResponseDto(
        order.getId(),
        games,
        order.getTotalPrice(),
        order.getCreatedAt(),
        order.getStatus()
    );
  }

}
