package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode {

  GAME_NOT_FOUND("게임을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  DOES_NOT_MATCH("게임을 생성한 유저와 일치하지 않습니다", HttpStatus.FORBIDDEN);

  private final String message;
  private final HttpStatus httpStatus;
}
