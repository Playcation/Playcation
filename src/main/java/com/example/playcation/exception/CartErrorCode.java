package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ExceptionType {
  GAME_ALREADY_IN_CART("해당 회원의 장바구니에 게임이 이미 존재합니다.", HttpStatus.CONFLICT),
  NO_GAME_IN_CART("회원의 장바구니에 해당 게임이 없습니다.", HttpStatus.NOT_FOUND);

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
