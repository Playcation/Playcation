package com.example.playcation.emailsender.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EmailRequestDto {

  @NotBlank(message = "이메일은 필수값입니다.")
  @Size(max = 100, message = "이메일은 최대 100글자 입니다.")
  @Pattern(regexp = "[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+$", message = "이메일 형식이 일치하지 않습니다.")
  private String email;
}
