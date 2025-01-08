package com.example.playcation.gametag.dto;

import com.example.playcation.game.entity.Game;
import lombok.Getter;

@Getter
public class GameTagRequestDto {

  private Long tagId;

  private Long gameId;

  public GameTagRequestDto(Long tagId, Long gameId) {
    this.tagId = tagId;
    this.gameId = gameId;
  }
}
