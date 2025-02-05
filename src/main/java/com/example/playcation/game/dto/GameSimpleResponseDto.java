package com.example.playcation.game.dto;

import com.example.playcation.game.entity.Game;
import com.example.playcation.order.entity.OrderDetail;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게임에 관한 간략한 정보를 리턴합니다.
 */
@Getter
@RequiredArgsConstructor
public class GameSimpleResponseDto {

  private final Long id;

  private final String imageUrl;

  private final String title;

  public static GameSimpleResponseDto orderDetailToDto(OrderDetail orderDetail) {

    Game game = orderDetail.getGame();
    return new GameSimpleResponseDto(
        game.getId(),
        game.getImageUrl(),
        game.getTitle()
    );
  }
}
