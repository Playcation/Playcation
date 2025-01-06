package com.example.playcation.game.repository;

import com.example.playcation.enums.GameStatus;
import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.GameException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom {

  default Game findByIdOrElseThrow(Long id) {
    Game game = findById(id).orElseThrow(() -> new NotFoundException(GameErrorCode.GAME_NOT_FOUND));
    if (!game.getStatus().equals(GameStatus.ON_SAL)) {
      throw new GameException(GameErrorCode.GAME_NOT_FOUND);
    }
    return game;
  }
}
