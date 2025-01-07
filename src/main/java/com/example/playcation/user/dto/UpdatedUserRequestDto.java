package com.example.playcation.user.dto;

import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class UpdatedUserRequestDto {

  private String password;

  @Column(nullable = true)
  private String name;

  @Column(nullable = true)
  private String description;

}
