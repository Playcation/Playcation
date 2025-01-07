package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum s3ErrorCode implements ExceptionType {

  NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
