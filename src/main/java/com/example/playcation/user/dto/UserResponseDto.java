package com.example.playcation.user.dto;

import com.example.playcation.user.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {

  private String email;

  private String filePath;

  private String username;

  private String description;

  private LocalDateTime updatedDate;

  public static UserResponseDto toDto(User user){
    return new UserResponseDto(
        user.getEmail(),
        user.getImageUrl(),
        user.getUsername(),
        user.getDescription(),
        user.getUpdatedAt()
    );
  }
}
