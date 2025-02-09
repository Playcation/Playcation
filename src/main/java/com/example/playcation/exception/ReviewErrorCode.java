package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ExceptionType{

  // NoAuthorizedException(인증되지 않음) 403
  NOT_AUTHOR_OF_REVIEW(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰가 아닙니다."),
  NOT_AUTHOR_OF_REVIEW_GAME(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 삭제 가능합니다."),
  REVIEW_ALREADY_EXISTS(HttpStatus.FORBIDDEN,"본인이 등록한 게임에 리뷰를 작성할 수 없습니다."),

  // NotFoundException(존재하지않는 리소스)
  GAME_NOT_IN_LIBRARY(HttpStatus.NOT_FOUND, "해당 게임을 구매하지 않았습니다."),
  NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
  LIKE_NOT_FOUND(HttpStatus.NOT_FOUND,"좋아요 기록이 존재하지 않습니다."),

  // DuplicatedException(중복된 항목이나, 이미 존재하는 리소스에 대해) 409
  ALREADY_REVEIW_LIKE(HttpStatus.CONFLICT,"이미 좋아요를 누른 리뷰입니다."),
  REVIEW_EXIST(HttpStatus.CONFLICT, "이미 해당 게임에 대한 리뷰를 작성하였습니다."),

  // InvalidInputException
  NO_LIKE_TO_REMOVE(HttpStatus.BAD_REQUEST, "좋아요가 0일 때는 더 이상 제거할 수 없습니다.");


  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }


}
