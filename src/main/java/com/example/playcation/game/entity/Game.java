package com.example.playcation.game.entity;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.GameStatus;
import com.example.playcation.game.dto.UpdateGameRequestDto;
import com.example.playcation.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
  private User user;

  private String title;

  private String category;

  private BigDecimal price;

  private String description;

  @Enumerated(value = EnumType.STRING)
  private GameStatus status;

  private String imageUrl;


  public Game(User user, String title, String category, BigDecimal price, String description, GameStatus status, String imageUrl) {
    this.user = user;
    this.title = title;
    this.category = category;
    this.price = price;
    this.description = description;
    this.status = status;
    this.imageUrl = imageUrl;
  }

  public void updateGame(UpdateGameRequestDto requestDto) {
    this.title = requestDto.getTitle();
    this.category = requestDto.getCategory();
    this.price = requestDto.getPrice();
    this.description = requestDto.getDescription();
    this.imageUrl = requestDto.getImageUrl();
  }

  public void deleteGame(GameStatus status) {
    this.status = status;
  }
}
