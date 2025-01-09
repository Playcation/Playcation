package com.example.playcation.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionController {

    //커스텀
    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> duplicatedException(DuplicatedException e) {
        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> internalServerException(InternalServerException e) {
        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> invalidInputException(InvalidInputException e) {
        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> notFoundException(NotFoundException e) {
        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> noAuthorizedException(NoAuthorizedException e) {
        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
    }

    //자바
    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> constrainViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(new ExceptionResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), "WRONG_INPUT", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(new ExceptionResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), "VALIDATION_EXCEPTION", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleNotFoundError(HttpServletRequest request) {
        return new ResponseEntity<>(new ExceptionResponseDto(HttpStatus.NOT_FOUND.getReasonPhrase(), "No Handler Found" , "The API you are trying to reach does not exist: " + request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new ExceptionResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Exception" , "An error occurred: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
