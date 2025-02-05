package com.example.playcation.game.dto;

import com.example.playcation.enums.GameStatus;
import com.example.playcation.game.entity.Game;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class GameResponseDto {

  private Long gameId;

  private Long userId;

  private String title;

  private Long categoryId;

  private BigDecimal price;

  private String description;

  private String mainImagePath;

  private List<String> subImagePath;

  private String gameFilePath;

  private GameStatus status;

  private LocalDateTime createdAt;

  private LocalDateTime updateAt;


  public GameResponseDto(Long gameId, Long userId, String title, Long categoryId,
      BigDecimal price, String description, String mainImagePath, List<String> subImagePath, String gameFilePath, GameStatus status,
      LocalDateTime createdAt, LocalDateTime updateAt) {
    this.gameId = gameId;
    this.userId = userId;
    this.title = title;
    this.categoryId = categoryId;
    this.price = price;
    this.description = description;
    this.mainImagePath = mainImagePath;
    this.subImagePath = subImagePath;
    this.gameFilePath = gameFilePath;
    this.status = status;
    this.createdAt = createdAt;
    this.updateAt = updateAt;
  }


  public static GameResponseDto toDto(Game game, List<String> subImagePath) {
    return new GameResponseDto(
        game.getId(),
        game.getUser().getId(),
        game.getTitle(),
        game.getCategory().getId(),
        game.getPrice(),
        game.getDescription(),
        game.getImageUrl(),
        subImagePath,
        game.getFilePath(),
        game.getStatus(),
        game.getCreatedAt(),
        game.getUpdatedAt()
    );
  }
}
