package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ExceptionType {

  PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다"),
  PAYMENT_AMOUNT_EXP(HttpStatus.BAD_REQUEST, "입력하신 금액과 결제 정보의 금액이 같지 않습니다"),
  ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "이미 승인된 요청입니다"),
  PAYMENT_NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "보유하신 포인트가 부족합니다");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
