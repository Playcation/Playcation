package com.example.playcation.tag.Dto;

import lombok.Getter;

@Getter
public class CreatedTagRequestDto {

  private String tagName;

  public CreatedTagRequestDto(String tagName) {
    this.tagName = tagName;
  }
}
