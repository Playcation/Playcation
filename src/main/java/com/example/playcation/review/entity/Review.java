package com.example.playcation.review.entity;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.game.entity.Game;
import com.example.playcation.library.entity.Library;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  private Library library;

  private String contents;

  @Enumerated(value = EnumType.STRING)
  private ReviewStatus status;

  private Long countLike;

}
