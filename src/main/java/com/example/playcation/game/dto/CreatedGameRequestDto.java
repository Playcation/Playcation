package com.example.playcation.game.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class CreatedGameRequestDto {

  private String title;

  private String category;

  private BigDecimal price;

  private String description;

  private String image;

  public CreatedGameRequestDto(String title, String category, BigDecimal price, String description, String image) {
    this.title = title;
    this.category = category;
    this.price = price;
    this.description = description;
    this.image = image;
  }
}
