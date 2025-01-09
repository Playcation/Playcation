package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ExceptionType {

  INVALID_ITEM_INCLUDED(HttpStatus.BAD_REQUEST, "유효하지 않은 게임이 포함되어 있습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
