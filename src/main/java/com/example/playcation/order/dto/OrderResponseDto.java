package com.example.playcation.order.dto;

import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class OrderResponseDto {

  private final UUID id;

  private final List<OrderDetailResponseDto> games;

  private final BigDecimal totalPrice;

  private final LocalDateTime orderedAt;

  public static OrderResponseDto toDto(Order order, List<OrderDetail> orderDetails) {
    return new OrderResponseDto(
        order.getId(),
        orderDetails.stream().map(OrderDetailResponseDto::toDto).toList(),
        order.getTotalPrice(),
        order.getCreatedAt()
    );
  }

}
