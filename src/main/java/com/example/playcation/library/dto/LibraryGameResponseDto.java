package com.example.playcation.library.dto;

import com.example.playcation.game.dto.CreatedGameResponseDto;
import com.example.playcation.game.entity.Game;
import java.util.List;
import lombok.Getter;

@Getter
public class LibraryGameResponseDto {

  private CreatedGameResponseDto game;

  private Boolean favourite;

  private Long count;

  public LibraryGameResponseDto(Game game, boolean favourite, Long count) {
    this.game = CreatedGameResponseDto.toDto(game);
    this.favourite = favourite;
    this.count = count;
  }
}
