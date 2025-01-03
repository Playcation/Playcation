package com.example.playcation.user.dto;

import com.example.playcation.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponseDto {

  private String email;

  private String token;

  public static UserLoginResponseDto toDto(User user, String token) {
    token= "Bearer " + token;
    return new UserLoginResponseDto(
        user.getEmail(),
        token
    );
  }

}
