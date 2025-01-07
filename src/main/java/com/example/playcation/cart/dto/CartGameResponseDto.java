package com.example.playcation.cart.dto;


import com.example.playcation.game.entity.Game;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartGameResponseDto {

  private Long id;
  private String title;
  private BigDecimal price;

  public static CartGameResponseDto toDto(Game game) {
    return new CartGameResponseDto(
        game.getId(),
        game.getTitle(),
        game.getPrice()
    );
  }
}