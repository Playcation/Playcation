package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GameTagErrorCode implements ExceptionType{

  GAME_TAG_NOT_FOUND("게임 태그를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  DUPLICATE_GAME_TAG("해당 게임에 이미 같은 태그가 등록되었습니다", HttpStatus.BAD_REQUEST),
  NO_PERMISSION("태그 삭제는 등록한 유저만 가능합니다", HttpStatus.UNAUTHORIZED);

  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
