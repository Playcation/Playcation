package com.example.playcation.cart.dto;

import com.example.playcation.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatedCartGameResponseDto {

  private final Long cartId;
  private final Long userId;
  private final Long gameId;

  public static UpdatedCartGameResponseDto toDto(Cart cart) {
    return new UpdatedCartGameResponseDto(
        cart.getId(),
        cart.getUser().getId(),
        cart.getGame().getId()
    );
  }
}