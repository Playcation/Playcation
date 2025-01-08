package com.example.playcation.game.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class UpdatedGameRequestDto {

  private String title;

  private String category;

  private BigDecimal price;

  private String description;

  public UpdatedGameRequestDto(String title, String category, BigDecimal price, String description) {
    this.title = title;
    this.category = category;
    this.price = price;
    this.description = description;
  }
}
