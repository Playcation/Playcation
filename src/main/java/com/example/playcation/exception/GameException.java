package com.example.playcation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GameException extends RuntimeException {
  private GameErrorCode gameErrorCode;
}
