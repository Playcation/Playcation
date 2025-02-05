package com.example.playcation.user.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatedUserRequestDto {

  private String username;

  private String password;

  @Column(nullable = true)
  private String description;

}
