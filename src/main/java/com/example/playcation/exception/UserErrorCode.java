package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ExceptionType {
  //InvalidInputException
  //비밀번호가 틀렸을 때 출력하는 오류 메시지
  WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀 번호가 틀렸습니다."),

  // NotFoundException
  NOT_FOUND_USER(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
  DELETED_USER(HttpStatus.BAD_REQUEST, "이미 삭제된 회원입니다."),

  // NoAuthorizedException
  NOT_AUTHORIZED_MANAGER(HttpStatus.UNAUTHORIZED, "유저만 권한을 변경 할 수 있습니다."),

  // DuplicatedException
  //중복된 이메일로 가일 할 때 출력하는 오류 메시지
  EMAIL_EXIST(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다.");

  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return "";
  }
}
