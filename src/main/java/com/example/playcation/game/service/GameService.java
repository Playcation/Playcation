package com.example.playcation.game.service;

import static com.example.playcation.enums.Auth.MANAGER;
import static com.example.playcation.enums.GameStatus.ON_SAL;

import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.GameException;
import com.example.playcation.game.Dto.CreatedGameRequestDto;
import com.example.playcation.game.Dto.CreatedGameResponseDto;
import com.example.playcation.game.Dto.PageGameResponseDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

  public CreatedGameResponseDto findGameById(Long gameId) {
    Game game = gameRepository.findByIdOrElseThrow(gameId);
    return CreatedGameResponseDto.toDto(game);
  }

  public PageGameResponseDto searchGames(int page, String title, String category, BigDecimal price, LocalDateTime createdAt) {

    PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Direction.DESC, "id"));

    return gameRepository.searchGames(pageRequest, title, category, price, createdAt);
  }
}
