package com.example.playcation.game.Dto;

import com.example.playcation.enums.GameStatus;
import com.example.playcation.game.entity.Game;
import com.example.playcation.gametag.entity.GameTag;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatedGameResponseEntity {

  private Long gameId;

  private Long userId;

  private String title;

  private String category;

  private BigDecimal price;

  private String description;

  private String image;

  private GameStatus status;

  private LocalDateTime createdAt;

  private LocalDateTime updateAt;

  @Builder
  public CreatedGameResponseEntity (Long gameId, Long userId, String title, String category, BigDecimal price, String description, String image, GameStatus status, LocalDateTime createdAt, LocalDateTime updateAt) {
    this.gameId = gameId;
    this.userId = userId;
    this.title = title;
    this.category = category;
    this.price = price;
    this.description = description;
    this.image = image;
    this.status = status;
    this.createdAt = createdAt;
    this.updateAt = updateAt;
  }

  @Builder
  public CreatedGameResponseEntity toDto(Game game) {
    return new CreatedGameResponseEntity(
        game.getId(),
        game.getUser().getId(),
        game.getTitle(),
        game.getCategory(),
        game.getPrice(),
        game.getDescription(),
        game.getImageUrl(),
        game.getStatus(),
        game.getCreatedAt(),
        game.getUpdatedAt()
    );
  }
}
