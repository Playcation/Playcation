package com.example.playcation.order.dto;

import com.example.playcation.enums.OrderStatus;
import com.example.playcation.order.entity.Refund;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefundResponseDto {

  private final Long orderDetailId;

  private final String refundMessage;

  private final OrderStatus status;

  public static RefundResponseDto toDto(Refund refund, OrderStatus status) {
    return new RefundResponseDto(
        refund.getOrderDetail().getId(),
        refund.getRefundMessage(),
        status
    );
  }
}
