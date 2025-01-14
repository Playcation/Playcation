package com.example.playcation.category.dto;

import lombok.Getter;

@Getter
public class CategoryRequestDto {

  private String categoryName;

  public CategoryRequestDto(String categoryName) {
    this.categoryName = categoryName;
  }
}
