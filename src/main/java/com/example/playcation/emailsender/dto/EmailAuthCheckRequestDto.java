package com.example.playcation.emailsender.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailAuthCheckRequestDto {

  @NotBlank(message = "인증번호를 입력해 주세요.")
  private String authNum;
}
