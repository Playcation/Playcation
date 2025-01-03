package com.example.playcation.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ExceptionResponseDto {

    private static final ObjectMapper objectmapper = new ObjectMapper();

    private String errorCode;

    private String message;

    public ExceptionResponseDto(ExceptionType exceptionType) {
        this.errorCode = exceptionType.getErrorName();
        this.message = exceptionType.getMessage();
    }

    public String convertToJson() throws JsonProcessingException {
        return objectmapper.writeValueAsString(this);
    }

}
