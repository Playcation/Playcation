package com.example.playcation.game.dto;

import com.example.playcation.category.entity.Category;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class UpdatedGameRequestDto {

  private String title;

  private Category category;

  private BigDecimal price;

  private String description;

  public UpdatedGameRequestDto(String title, Category category, BigDecimal price, String description) {
    this.title = title;
    this.category = category;
    this.price = price;
    this.description = description;
  }
}
