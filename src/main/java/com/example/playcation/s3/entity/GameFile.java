package com.example.playcation.s3.entity;

import com.example.playcation.game.entity.Game;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "game_file")
public class GameFile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String bucket;

  @ManyToOne(fetch = FetchType.LAZY)
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  private FileDetail fileDetail;

  public GameFile(Game game, FileDetail fileDetail, String bucket) {
    this.game = game;
    this.fileDetail = fileDetail;
    this.bucket = bucket;
  }

}
