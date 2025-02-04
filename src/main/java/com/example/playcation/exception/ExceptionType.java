package com.example.playcation.exception;

import jakarta.servlet.http.HttpServlet;
import org.springframework.http.HttpStatus;

public interface ExceptionType {

  HttpStatus getHttpStatus();

  String getErrorName();

  String getMessage();
}
