package com.example.playcation.emailsender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResult {
  private String code;
  private String message;
}
