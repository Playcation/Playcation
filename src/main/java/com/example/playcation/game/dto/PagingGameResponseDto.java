package com.example.playcation.game.dto;

import com.example.playcation.game.entity.Game;
import java.util.List;
import lombok.Getter;

@Getter
public class PagingGameResponseDto {

  private List<CreatedGameResponseDto> gameList;
  private Long count;

  public PagingGameResponseDto(List<Game> gameList, Long count) {
    this.gameList = gameList.stream()
        .map(CreatedGameResponseDto::toDto)
        .toList();
    this.count = count;
  }
}
