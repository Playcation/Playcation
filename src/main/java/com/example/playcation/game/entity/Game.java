package com.example.playcation.game.entity;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.GameStatus;
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

@Entity
@Table(name = "`game`")
public class Game extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  private String title;

  private String category;

  private BigDecimal price;

  @Enumerated(value = EnumType.STRING)
  private GameStatus status;

  private String ImageUrl;
}
