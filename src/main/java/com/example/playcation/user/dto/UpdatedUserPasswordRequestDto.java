package com.example.playcation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatedUserPasswordRequestDto {

  private String oldPassword;

  private String newPassword;

}
