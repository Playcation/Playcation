package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ExceptionType {

  NOT_FOUND_CATEGORY("카테고리를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  DUPLICATE_CATEGORY("중복된 카테고리입니다", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
