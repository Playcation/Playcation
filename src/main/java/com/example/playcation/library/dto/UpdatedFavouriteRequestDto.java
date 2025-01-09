package com.example.playcation.library.dto;

import com.example.playcation.game.dto.UpdatedGameRequestDto;
import lombok.Getter;

@Getter
public class UpdatedFavouriteRequestDto {

  private boolean favourite;

  public UpdatedFavouriteRequestDto(boolean favourite) {
    this.favourite = favourite;
  }
}
