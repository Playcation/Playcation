package com.example.playcation.library.dto;

import com.example.playcation.library.entity.Library;
import java.util.List;
import lombok.Getter;

@Getter
public class LibraryListResponseDto {

  private List<Library> libraryList;

  private Long count;

  public LibraryListResponseDto(List<Library> libraryList, Long count) {
    this.libraryList = libraryList;
    this.count = count;
  }
}
