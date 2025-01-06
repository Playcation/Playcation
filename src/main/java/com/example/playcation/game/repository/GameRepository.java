package com.example.playcation.game.repository;

import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.GameException;
import com.example.playcation.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
  default Game findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new GameException(GameErrorCode.GAME_NOT_FOUND));
  }
}
