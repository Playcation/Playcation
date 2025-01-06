package com.example.playcation.game.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class UpdateGameRequestDto {

  private String title;

  private String category;

  private BigDecimal price;

  private String description;

  private String imageUrl;

  public UpdateGameRequestDto(String title, String category, BigDecimal price, String description, String imageUrl) {
    this.title = title;
    this.category = category;
    this.price = price;
    this.description = description;
    this.imageUrl = imageUrl;
  }
}
