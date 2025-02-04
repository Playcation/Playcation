package com.example.playcation.toss.dto;

import com.example.playcation.enums.PayType;
import com.example.playcation.toss.entity.Payment;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentDto {

  @NotNull
  private PayType payType;

  @NotNull
  private BigDecimal amount;

  @NotNull
  private String orderName;

  private String yourSuccessUrl;

  private String yourFailUrl;

  public Payment toEntity() {
    return Payment.builder()
        .payType(payType)
        .amount(amount)
        .orderName(orderName)
        .orderId(UUID.randomUUID().toString())
        .paySuccessYN(false)
        .build();
  }

}
