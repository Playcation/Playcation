package com.example.playcation.game.dto;

import com.example.playcation.game.entity.Game;
import com.example.playcation.s3.entity.FileDetail;
import java.util.List;
import lombok.Getter;

@Getter
public class PagingGameResponseDto {

  private List<Game> gameList;
  private Long count;

  public PagingGameResponseDto(List<Game> gameList, Long count) {
    this.gameList = gameList;
    this.count = count;
  }
}
