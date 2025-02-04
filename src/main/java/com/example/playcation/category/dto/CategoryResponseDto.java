package com.example.playcation.category.dto;

import com.example.playcation.category.entity.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDto {

  private Long categoryId;

  private String categoryName;

  public CategoryResponseDto(Long categoryId, String categoryName) {
    this.categoryId = categoryId;
    this.categoryName = categoryName;
  }

  public static CategoryResponseDto toDto(Category category) {
    return new CategoryResponseDto(
        category.getId(),
        category.getCategoryName()
    );
  }
}
