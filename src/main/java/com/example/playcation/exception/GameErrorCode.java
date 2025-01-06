package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode {

  GAME_NOT_FOUND("게임을 찾을 수 없습니다", HttpStatus.NOT_FOUND);

  private final String message;
  private final HttpStatus httpStatus;
}
