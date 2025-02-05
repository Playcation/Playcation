package com.example.playcation.toss.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResDto {

  private String payType;

  private BigDecimal amount;

  private String orderName;

  private String orderId;

  private String customerEmail;

  private String customerName;

  private String successUrl;

  private String failUrl;

  private String failReason;

  private boolean cancelYN;

  private String cancelReason;

  private String createdAt;

  public void updateSuccessUrl(String successUrl) {
    this.successUrl = successUrl;
  }

  public void updateFailUrl(String failUrl) {
    this.failUrl = failUrl;
  }
}