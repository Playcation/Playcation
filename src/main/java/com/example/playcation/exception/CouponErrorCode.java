package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ExceptionType {
  COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COUPON_OUT_OF_STOCK("쿠폰 재고가 없습니다.", HttpStatus.CONFLICT),
  DUPLICATE_COUPON("이미 존재하는 쿠폰입니다.", HttpStatus.BAD_REQUEST);


  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}