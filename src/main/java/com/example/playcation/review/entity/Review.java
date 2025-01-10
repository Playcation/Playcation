package com.example.playcation.review.entity;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.game.entity.Game;
import com.example.playcation.library.entity.Library;
import jakarta.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id")
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library_id")
  private Library library;

  @Column(length = 250)
  private String content;

  @Enumerated(value = EnumType.STRING)
  private ReviewStatus rating;

  private Long countLike = 0L;

  public void updateContent(String content, ReviewStatus rating) {
    this.content = content;
    this.rating = rating;
  }
}
