package com.example.playcation.oauth2.dto;

import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

  private String email;

  private String name;

  private String password;

  private Role role;

  public UserDto(String email, String name, Role role){
    this.email = email;
    this.name = name;
    this.role = role;
    this.password = Social.DEFAULT_PASSWORD.getPassword();
  }
}
