package com.example.playcation.gametag.dto;

import com.example.playcation.gametag.entity.GameTag;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GameTagResponseDto {

  private Long gameTagId;

  private Long tagId;

  private Long gameId;

  public GameTagResponseDto(Long gameTagId, Long tagId, Long gameId) {
    this.gameTagId = gameTagId;
    this.tagId = tagId;
    this.gameId = gameId;
  }

  public static GameTagResponseDto toDto(GameTag gameTag) {
    return new GameTagResponseDto(
        gameTag.getId(),
        gameTag.getTag().getId(),
        gameTag.getGame().getId()
    );
  }


}
