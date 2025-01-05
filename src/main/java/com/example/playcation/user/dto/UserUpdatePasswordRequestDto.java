package com.example.playcation.user.dto;

import lombok.Getter;

@Getter
public class UserUpdatePasswordRequestDto {

  private String oldPassword;

  private String newPassword;

}
