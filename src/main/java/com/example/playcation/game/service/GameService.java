package com.example.playcation.game.service;

import static com.example.playcation.enums.Auth.MANAGER;
import static com.example.playcation.enums.GameStatus.ON_SAL;

import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.GameException;
import com.example.playcation.game.Dto.CreatedGameRequestDto;
import com.example.playcation.game.Dto.CreatedGameResponseDto;
import com.example.playcation.game.Dto.PageGameResponseDto;
import com.example.playcation.game.Dto.UpdateGameRequestDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

  private final UserRepository userRepository;
  private final GameRepository gameRepository;


  // 게임 생성
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

  // 게임 단건 조회
  public CreatedGameResponseDto findGameById(Long gameId) {
    Game game = gameRepository.findByIdOrElseThrow(gameId);
    return CreatedGameResponseDto.toDto(game);
  }

  // 게임 다건 조회
  public PageGameResponseDto searchGames(int page, String title, String category, BigDecimal price, LocalDateTime createdAt) {

    // 페이징시 최대 출력 갯수와 정렬조건 설정
    PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Direction.DESC, "id"));

    return gameRepository.searchGames(pageRequest, title, category, price, createdAt);
  }

  // 게임 수정
  public CreatedGameResponseDto updateGame(Long gameId, Long userId, UpdateGameRequestDto requestDto) {

    Game game = gameRepository.findByIdOrElseThrow(gameId);

    // 현재 접속한 유저가 게임을 생성한 유저가 맞는지 비교
    if (!game.getUser().getId().equals(userId)) {
      throw new GameException(GameErrorCode.DOES_NOT_MATCH);
    }

    game.updateGame(requestDto);
    gameRepository.save(game);
    return CreatedGameResponseDto.toDto(game);
  }
}
