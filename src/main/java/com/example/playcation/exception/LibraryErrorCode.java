package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum LibraryErrorCode implements ExceptionType{

  NOT_FOUND_LIBRARY(HttpStatus.NOT_FOUND, "라이브러리를 찾을 수 없습니다."),
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다"),
  CANNOT_BE_MODIFIED(HttpStatus.FORBIDDEN, "본인의 라이브러리만 수정 가능합니다");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
