package com.example.playcation.gametag.entity;

import com.example.playcation.game.entity.Game;
import com.example.playcation.tag.entity.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "`game_tag`")
public class GameTag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Tag tag;

  @ManyToOne(fetch = FetchType.LAZY)
  private Game game;

}
