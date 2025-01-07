package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ExceptionType {
  EXIST_CART(HttpStatus.CONFLICT, "장바구니가 이미 존재합니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return "";
  }
}
