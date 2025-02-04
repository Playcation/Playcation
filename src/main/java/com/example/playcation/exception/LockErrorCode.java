package com.example.playcation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LockErrorCode implements ExceptionType {
  LOCK_NOT_AVAILABLE(HttpStatus.LOCKED, "락을 획득할 수 없습니다."),
  UNLOCKING_A_LOCK_WHICH_IS_NOT_LOCKED(HttpStatus.INTERNAL_SERVER_ERROR,
      "이미 종료된 락을 unlocking 하려고 시도하였습니다."),
  LOCK_INTERRUPTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "스레드 인터럽트 에러 발생");


  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getErrorName() {
    return this.name();
  }
}
