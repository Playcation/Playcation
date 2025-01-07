package com.example.playcation.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
public class CartGameDto {

  private final Long cartId;
  private final Long userId;
  private final List<GameInfo> games;

  // 내부 클래스: 게임 정보를 담는 DTO
  @Getter
  public static class GameInfo {

    private final Long id;
    private final String title;
    private final BigDecimal price;

    public GameInfo(Long id, String title, BigDecimal price) {
      this.id = id;
      this.title = title;
      this.price = price;
    }
  }

  // Cart -> CartGameDto 변환 메서드
  public CartGameDto(Long cartId, Long userId, List<GameInfo> games) {
    this.cartId = cartId;
    this.userId = userId;
    this.games = games;
  }
}