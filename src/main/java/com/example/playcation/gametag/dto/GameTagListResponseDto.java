package com.example.playcation.gametag.dto;

import com.example.playcation.gametag.entity.GameTag;
import java.util.List;
import lombok.Getter;

@Getter
public class GameTagListResponseDto {

  private List<GameTag> gameTagList;

  private Long count;

  public GameTagListResponseDto(List<GameTag> gameTagList, Long count) {
    this.gameTagList = gameTagList;
    this.count = count;
  }
}
