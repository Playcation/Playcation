package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ExceptionType {
  EVENT_NOT_FOUND("이벤트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COUPON_OUT_OF_STOCK("쿠폰 재고가 없습니다.", HttpStatus.CONFLICT),
  NO_AUTHORIZED_COUPON("ADMIN은 발급받을 수 없습니다.", HttpStatus.FORBIDDEN),
  DUPLICATE_ISSUED_COUPON("이미 해당 사용자에게 발급한 쿠폰입니다.", HttpStatus.BAD_REQUEST),
  DUPLICATED_REQUESTED_COUPON("이미 쿠폰을 요청하였습니다.", HttpStatus.BAD_REQUEST),
  REQUEST_USER_NOT_FOUND("요청한 사용자가 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_COUPON("이미 존재하는 쿠폰입니다.", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}