package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ExceptionType {

  INVALID_ITEM_INCLUDED(HttpStatus.BAD_REQUEST, "유효하지 않은 게임이 포함되어 있습니다."),
  EMPTY_CART(HttpStatus.BAD_REQUEST, "장바구니가 비어있습니다."),
  NO_EXIST_ORDER_DETAIL(HttpStatus.BAD_REQUEST, "주문 내에 해당 주문 상세 정보가 없습니다."),
  NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "id에 해당하는 주문 정보가 없습니다."),
  NOT_FOUND_ORDER_DETAIL(HttpStatus.NOT_FOUND, "id에 해당하는 주문 상세 정보가 없습니다."),
  REFUND_PERIOD_EXPIRED(HttpStatus.FORBIDDEN, "환불 가능 기간이 초과되어 요청이 거부되었습니다."),
  NO_AUTHORIZED_ORDER(HttpStatus.FORBIDDEN, "주문 정보에 접근할 권한이 부족합니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
