package com.example.playcation.game.entity;

import com.example.playcation.category.entity.Category;
import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.GameStatus;
import com.example.playcation.game.dto.UpdatedGameRequestDto;
import com.example.playcation.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "`game`")
@Getter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Game extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  private BigDecimal price;

  private String description;

  @Enumerated(value = EnumType.STRING)
  private GameStatus status;

  private String imageUrl;

  private String filePath;

  private LocalDateTime deletedAt;


  public Game(User user, String title, Category category, BigDecimal price, String description,
      GameStatus status, String imageUrl, String filePath) {
    this.user = user;
    this.title = title;
    this.category = category;
    this.price = price;
    this.description = description;
    this.status = status;
    this.imageUrl = imageUrl;
    this.filePath = filePath;
  }

  public void updateGame(UpdatedGameRequestDto requestDto, String imageUrl, String filePath) {
    this.title = requestDto.getTitle();
    this.category = requestDto.getCategory();
    this.price = requestDto.getPrice();
    this.description = requestDto.getDescription();
    this.imageUrl = imageUrl;
    this.filePath = filePath;
  }

  public void deleteGame() {
    this.status = GameStatus.OFF_SAL;
  }

  /**
   * 게임이 삭제되었는지 여부를 반환
   */
  public boolean isDeleted() {
    return (this.deletedAt == null);
  }
}
