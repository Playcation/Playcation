package com.example.playcation.emailsender.dto;


import lombok.Getter;

@Getter
public class EmailAuthResponseDto {
  private String code; // 인증번호

  public EmailAuthResponseDto(String code) {
    this.code = code;
  }
}