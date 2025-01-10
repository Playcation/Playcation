package com.example.playcation.library.dto;

import com.example.playcation.game.dto.CreatedGameResponseDto;
import com.example.playcation.game.entity.Game;
import java.util.List;
import lombok.Getter;

@Getter
public class LibraryGameResponseDto {

  private CreatedGameResponseDto game;

  private Boolean favourite;

  public LibraryGameResponseDto(Game game, boolean favourite) {
    this.game = CreatedGameResponseDto.toDto(game);
    this.favourite = favourite;
  }
}
