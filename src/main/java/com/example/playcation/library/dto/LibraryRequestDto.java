package com.example.playcation.library.dto;

import com.example.playcation.game.entity.Game;
import com.example.playcation.user.entity.User;
import lombok.Getter;

@Getter
public class LibraryRequestDto {

  private Long gameId;

  public LibraryRequestDto(Long gameId) {
    this.gameId = gameId;
  }

}
