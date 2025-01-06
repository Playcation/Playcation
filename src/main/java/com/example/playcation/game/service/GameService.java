package com.example.playcation.game.service;

import static com.example.playcation.enums.Auth.MANAGER;
import static com.example.playcation.enums.GameStatus.ON_SAL;

import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.GameException;
import com.example.playcation.game.Dto.CreatedGameRequestDto;
import com.example.playcation.game.Dto.CreatedGameResponseDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final UserRepository userRepository;
  private final GameRepository gameRepository;

  public GameService(UserRepository userRepository, GameRepository gameRepository) {
    this.userRepository = userRepository;
    this.gameRepository = gameRepository;
  }

  public CreatedGameResponseDto createdGame(Long id, CreatedGameRequestDto requestDto) {

    User user = userRepository.findByIdOrElseThrow(id);

    Game game = Game.builder()
        .user(user)
        .title(requestDto.getTitle())
        .category(requestDto.getCategory())
        .price(requestDto.getPrice())
        .description(requestDto.getDescription())
        .status(ON_SAL)
        .imageUrl(requestDto.getImage())
        .build();

    gameRepository.save(game);

    return CreatedGameResponseDto.toDto(game);
  }
}
