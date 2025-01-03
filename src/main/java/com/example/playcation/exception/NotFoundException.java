package com.example.playcation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    public NotFoundException(ExceptionType exceptionType) {
        this.httpStatus = exceptionType.getHttpStatus();
        this.errorCode = exceptionType.getErrorName();
        this.message = exceptionType.getMessage();
    }
}
