package com.example.playcation.cart.dto;

import com.example.playcation.cart.entity.Cart;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 장바구니에 게임 추가 및 삭제 시 필요한 Dto
 */
@Getter
@RequiredArgsConstructor
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