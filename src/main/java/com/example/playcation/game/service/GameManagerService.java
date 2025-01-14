package com.example.playcation.game.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameManagerService {

  private final GameRepository gameRepository;

  /**
   * 유저가 현재 판매중인 게임 목록을 반환
   */
  public List<CartGameResponseDto> findSellingGames(Long userId) {

    List<Game> games = gameRepository.findAllByUserId(userId);
    return games.stream().map(CartGameResponseDto::toDto).toList();
  }
}
