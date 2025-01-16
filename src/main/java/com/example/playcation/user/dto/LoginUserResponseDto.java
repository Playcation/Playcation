package com.example.playcation.user.dto;

import com.example.playcation.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginUserResponseDto {

  private String email;

  private String token;

  public static LoginUserResponseDto toDto(User user, String token) {
    return new LoginUserResponseDto(
        user.getEmail(),
        token
    );
  }
}
