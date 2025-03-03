package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode implements ExceptionType{

  GAME_NOT_FOUND("게임을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  DOES_NOT_MATCH("게임을 생성한 유저와 일치하지 않습니다", HttpStatus.FORBIDDEN),
  DUPLICATE_GAME_TITLE("이미 해당 게임이 존재합니다", HttpStatus.BAD_REQUEST),
  REVIEW_GAME_NOT_FOUND("리뷰와 연결된 게임을 찾을 수 없습니다.",HttpStatus.NOT_FOUND);

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
