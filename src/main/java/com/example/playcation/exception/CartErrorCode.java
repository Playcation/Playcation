package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ExceptionType {
  GAME_ALREADY_IN_CART(HttpStatus.CONFLICT, "해당 회원의 장바구니에 게임이 이미 존재합니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return "";
  }
}
