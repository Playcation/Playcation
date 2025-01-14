package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagErrorCode implements ExceptionType{

  TAG_NOT_FOUND("태그를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  DUPLICATE_TAG("이미 존재하는 태그입니다", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
