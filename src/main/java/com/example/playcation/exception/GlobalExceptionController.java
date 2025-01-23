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
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionController {

    //커스텀
    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> duplicatedException(DuplicatedException e) {
//        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
        return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> internalServerException(InternalServerException e) {
//        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
        return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> invalidInputException(InvalidInputException e) {
//        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
        return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> notFoundException(NotFoundException e) {
//        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
        return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> noAuthorizedException(NoAuthorizedException e) {
//        return new ResponseEntity<>(new ExceptionResponseDto(e.getHttpStatus().toString(), e.getErrorName(), e.getMessage()), e.getHttpStatus());
        return createErrorResponse(e.getHttpStatus(), e.getErrorName(), e.getMessage());
    }

    //자바
    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> constrainViolationException(ConstraintViolationException e) {
//        return new ResponseEntity<>(new ExceptionResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), "WRONG_INPUT", e.getMessage()), HttpStatus.BAD_REQUEST);
        return createErrorResponse(HttpStatus.BAD_REQUEST, "WRONG_INPUT", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponseDto> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
//        return new ResponseEntity<>(new ExceptionResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), "VALIDATION_EXCEPTION", message), HttpStatus.BAD_REQUEST);
        return createErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_EXCEPTION", message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleNotFoundError(HttpServletRequest request) {
//        return new ResponseEntity<>(new ExceptionResponseDto(HttpStatus.NOT_FOUND.getReasonPhrase(), "No Handler Found" , "The API you are trying to reach does not exist: " + request.getRequestURI()), HttpStatus.NOT_FOUND);
        return createErrorResponse(HttpStatus.NOT_FOUND, "No Handler Found", "The API you are trying to reach does not exist: " + request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleGenericException(Exception ex) {
//        return new ResponseEntity<>(new ExceptionResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Exception" , "An error occurred: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Exception", "An error occurred: " + ex.getMessage());
    }

    private ResponseEntity<ExceptionResponseDto> createErrorResponse(HttpStatus status, String errorName, String message) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(
            status.getReasonPhrase(),
            errorName,
            message
        );
        return new ResponseEntity<>(responseDto, status);
    }

}
