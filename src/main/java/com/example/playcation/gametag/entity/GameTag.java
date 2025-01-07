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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Builder
@Table(name = "`game_tag`")
public class GameTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Tag tag;

  @ManyToOne(fetch = FetchType.LAZY)
  private Game game;

  public void updateGameTag(Tag tag) {
    this.tag = tag;
  }

}
