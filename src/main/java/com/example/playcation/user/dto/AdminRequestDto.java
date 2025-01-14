package com.example.playcation.user.dto;

import com.example.playcation.enums.Role;
import lombok.Getter;

@Getter
public class AdminRequestDto {

  private String username;

  private String password;

  private Role role;

}
