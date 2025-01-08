package com.example.playcation.gametag.dto;

import com.example.playcation.game.dto.CreatedGameResponseDto;
import com.example.playcation.game.entity.Game;
import java.util.List;

public class GameListResponseDto {

  private List<CreatedGameResponseDto> gameList;

  public GameListResponseDto(List<Game> gameList) {
    this.gameList = gameList.stream()
        .map(CreatedGameResponseDto::toDto)
        .toList();
  }

}
