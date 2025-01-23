package com.example.playcation.emailsender.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailCheckDto {
  // 사용자가 인증번호를 확인하고 인증번호를 입력하였을 때 받아오는 DTO

  @NotBlank(message = "이메일은 필수값입니다.")
  private String email;

  @NotBlank(message = "인증 번호를 입력해 주세요")
  private String authNum;

  }