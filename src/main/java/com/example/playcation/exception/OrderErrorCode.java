package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ExceptionType {

  INVALID_ITEM_INCLUDED(HttpStatus.BAD_REQUEST, "유효하지 않은 게임이 포함되어 있습니다."),
  NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "id에 해당하는 주문 정보가 없습니다."),
  NO_AUTHORIZED_ORDER(HttpStatus.FORBIDDEN, "주문 정보에 접근할 권한이 부족합니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
