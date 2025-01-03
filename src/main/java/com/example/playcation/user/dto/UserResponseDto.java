package com.example.playcation.user.dto;

import com.example.playcation.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {

  private String email;

  public static UserResponseDto toDto(User user){
    return new UserResponseDto(user.getEmail());
  }

}
