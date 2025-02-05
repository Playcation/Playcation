package com.example.playcation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidInputException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorName;
    private final String message;

    public InvalidInputException(ExceptionType exceptionType) {
        this.httpStatus = exceptionType.getHttpStatus();
        this.errorName = exceptionType.getErrorName();
        this.message = exceptionType.getMessage();
    }
}
