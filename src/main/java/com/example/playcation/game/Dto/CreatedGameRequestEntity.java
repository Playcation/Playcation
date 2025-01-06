package com.example.playcation.game.Dto;

import java.math.BigDecimal;

public class CreatedGameRequestEntity {

  private String title;

  private String category;

  private BigDecimal price;

  private String description;

  private String image;

  public CreatedGameRequestEntity (String title, String category, BigDecimal price, String description, String image) {
    this.title = title;
    this.category = category;
    this.price = price;
    this.description = description;
    this.image = image;
  }
}
