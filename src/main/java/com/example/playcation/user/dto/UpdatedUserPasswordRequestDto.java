package com.example.playcation.user.dto;

import lombok.Getter;

@Getter
public class UpdatedUserPasswordRequestDto {

  private String oldPassword;

  private String newPassword;

}
