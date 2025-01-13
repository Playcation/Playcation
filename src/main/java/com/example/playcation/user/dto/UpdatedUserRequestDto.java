package com.example.playcation.user.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatedUserRequestDto {

  private String password;

  @Column(nullable = true)
  private String name;

  @Column(nullable = true)
  private String description;

}
