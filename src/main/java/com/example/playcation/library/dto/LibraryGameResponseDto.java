package com.example.playcation.library.dto;

import com.example.playcation.game.entity.Game;
import lombok.Getter;

@Getter
public class LibraryGameResponseDto {

  private Game game;

  private Boolean isFavourite;

  private Long count;

  public LibraryGameResponseDto(Game game, Boolean isFavourite, Long count) {
    this.game = game;
    this.isFavourite = isFavourite;
    this.count = count;
  }
}
