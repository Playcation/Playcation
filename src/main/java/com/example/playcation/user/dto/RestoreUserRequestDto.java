package com.example.playcation.user.dto;

import lombok.Getter;

@Getter
public class RestoreUserRequestDto {

  private String email;

  private String name;

  private String password;
}
