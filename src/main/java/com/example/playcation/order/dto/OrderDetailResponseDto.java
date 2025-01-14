package com.example.playcation.order.dto;

import com.example.playcation.enums.OrderStatus;
import com.example.playcation.order.entity.OrderDetail;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetailResponseDto {

  private final Long id;
  private final Long gameId;
  private final String imageUrl;
  private final String title;
  private final BigDecimal price;
  private final OrderStatus status;
  private String refundMessage;

  public static OrderDetailResponseDto toDto(OrderDetail orderDetail) {
    OrderDetailResponseDto build = OrderDetailResponseDto.builder()
        .id(orderDetail.getId())
        .gameId(orderDetail.getGame().getId())
        .imageUrl(orderDetail.getGame().getImageUrl())
        .title(orderDetail.getGame().getTitle())
        .price(orderDetail.getPrice())
        .status(orderDetail.getStatus())
        .build();

    if (orderDetail.getRefund() != null) {
      build.refundMessage = orderDetail.getRefund().getRefundMessage();
    }

    return build;
  }
}
