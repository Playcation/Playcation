package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GameTagErrorCode implements ExceptionType{

  GAME_TAG_NOT_FOUND("태그를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  DUPLICATE_GAME_TAG("해당 게임에 이미 같은 태그가 등록되었습니다", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
