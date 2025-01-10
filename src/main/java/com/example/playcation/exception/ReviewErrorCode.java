package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ExceptionType{

  NOT_AUTHOR_OF_REVIEW(HttpStatus.UNAUTHORIZED, "본인이 작성한 리뷰만 삭제할 수 있습니다."),
  GAME_NOT_IN_LIBRARY(HttpStatus.UNAUTHORIZED, "해당 게임을 구매하지 않았습니다."),

  NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),

  RATING_EXIST(HttpStatus.BAD_REQUEST, "평가란을 입력하지 않았습니다."),
  REVIEW_EXIST(HttpStatus.BAD_REQUEST, "이미 해당 게임에 대한 리뷰를 작성하였습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }


}
