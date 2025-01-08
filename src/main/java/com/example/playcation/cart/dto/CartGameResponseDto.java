package com.example.playcation.cart.dto;


import com.example.playcation.game.entity.Game;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 장바구니 내 게임 전체 조회 시 개별 게임 정보 Dto
 */
@Getter
@RequiredArgsConstructor
public class CartGameResponseDto {

  private final Long id;
  private final String imageUrl;
  private final String title;
  private final BigDecimal price;


  public static CartGameResponseDto toDto(Game game) {
    return new CartGameResponseDto(
        game.getId(),
        game.getImageUrl(),
        game.getTitle(),
        game.getPrice()
    );
  }
}