package com.example.playcation.gametag.dto;

import com.example.playcation.game.dto.CreatedGameResponseDto;
import com.example.playcation.game.entity.Game;
import java.util.List;
import lombok.Getter;

@Getter
public class GameListResponseDto {

  private List<CreatedGameResponseDto> gameList;

  private Long count;

  public GameListResponseDto(List<Game> gameList, Long count) {
    this.gameList = gameList.stream()
        .map(CreatedGameResponseDto::toDto)
        .toList();
    this.count = count;
  }

}
