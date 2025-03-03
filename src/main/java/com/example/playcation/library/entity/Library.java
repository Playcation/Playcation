package com.example.playcation.library.entity;


import com.example.playcation.common.BaseEntity;
import com.example.playcation.game.entity.Game;
import com.example.playcation.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`library`")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Library extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  private Game game;

  private Boolean favourite;

  public void updateFavourite(boolean Favourite) {
    this.favourite = Favourite;
  }
}
