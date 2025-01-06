package com.example.playcation.game.dto;

import com.example.playcation.game.entity.Game;
import java.util.List;
import lombok.Getter;

@Getter
public class PageGameResponseDto {

  private List<CreatedGameResponseDto> gameList;
  private Long count;

  public PageGameResponseDto(List<Game> gameList, Long count) {
    this.gameList = gameList.stream()
        .map(CreatedGameResponseDto::toDto)
        .toList();
    this.count = count;
  }
}
