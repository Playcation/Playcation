package com.example.playcation.game.repository;

import com.example.playcation.enums.GameStatus;
import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.game.entity.Game;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom {

  default Game findByIdOrElseThrow(Long id) {
    Game game = findById(id).orElseThrow(() -> new NotFoundException(GameErrorCode.GAME_NOT_FOUND));
    if (!game.getStatus().equals(GameStatus.ON_SAL)) {
      throw new NotFoundException(GameErrorCode.GAME_NOT_FOUND);
    }
    return game;
  }

  List<Game> findAllByIdIn(List<Long> ids);

  boolean existsByIdAndUserId(Long id, Long userId);
}
