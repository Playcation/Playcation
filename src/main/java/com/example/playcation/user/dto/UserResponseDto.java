package com.example.playcation.user.dto;

import com.example.playcation.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {

  private String email;

  private String filePath;

  private String name;

  private String description;

  public static UserResponseDto toDto(User user){
    return new UserResponseDto(
        user.getEmail(),
        user.getImageUrl(),
        user.getName(),
        user.getDescription()
    );
  }
}
