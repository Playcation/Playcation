package com.example.playcation.oauth2.dto;

import com.example.playcation.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

  private String email;

  private String name;

  private Role role;

}
