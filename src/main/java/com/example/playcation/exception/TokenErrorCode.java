package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ExceptionType {

  // NoAuthorizedException
  NO_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "다시 로그인해주시기 바랍니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
