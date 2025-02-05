package com.example.playcation.game.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class CreatedGameRequestDto {

  private String title;

  private Long categoryId;

  private BigDecimal price;

  private String description;

  private String image;

  public CreatedGameRequestDto(String title, Long categoryId, BigDecimal price, String description,
      String image) {
    this.title = title;
    this.categoryId = categoryId;
    this.price = price;
    this.description = description;
    this.image = image;
  }
}
