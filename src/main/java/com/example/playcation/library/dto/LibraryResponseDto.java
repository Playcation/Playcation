package com.example.playcation.library.dto;

import com.example.playcation.library.entity.Library;
import lombok.Getter;

@Getter
public class LibraryResponseDto {

  private Long libraryId;

  private Long userId;

  private Long gameId;

  private Boolean isFavourite;

  public LibraryResponseDto(Long libraryId, Long userId, Long gameId, Boolean isFavourite) {
    this.libraryId = libraryId;
    this.userId = userId;
    this.gameId = gameId;
    this.isFavourite = isFavourite;
  }

  public static LibraryResponseDto toDto(Library library) {
    return new LibraryResponseDto(
        library.getId(),
        library.getUser().getId(),
        library.getGame().getId(),
        library.getFavourite()
    );
  }
}
