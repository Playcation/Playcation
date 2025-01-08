package com.example.playcation.library.dto;

import lombok.Getter;

@Getter
public class LibraryRequestDto {

  private Long userId;

  private Long gameId;

  private String name;

  public LibraryRequestDto(Long userId, Long gameId, String name) {
    this.userId = userId;
    this.gameId = gameId;
    this.name = name;
  }

}
