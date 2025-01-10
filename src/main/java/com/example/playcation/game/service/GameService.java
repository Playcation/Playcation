package com.example.playcation.game.service;


import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.GameStatus;
import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.game.dto.CreatedGameRequestDto;
import com.example.playcation.game.dto.CreatedGameResponseDto;
import com.example.playcation.game.dto.PagingGameResponseDto;
import com.example.playcation.game.dto.UpdatedGameRequestDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.gametag.entity.GameTag;
import com.example.playcation.gametag.repository.GameTagRepository;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.repository.LibraryRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
  private final LibraryRepository libraryRepository;
  private final GameTagRepository gameTagRepository;


  // 게임 생성
  @Transactional
  public CreatedGameResponseDto createGame(Long id,
      CreatedGameRequestDto requestDto) {

    User user = userRepository.findByIdOrElseThrow(id);

    Game game = Game.builder()
        .user(user)
        .title(requestDto.getTitle())
        .category(requestDto.getCategory())
        .price(requestDto.getPrice())
        .description(requestDto.getDescription())
        .status(GameStatus.ON_SAL)
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
  public PagingDto<CreatedGameResponseDto> searchGames(int page, String title, String category, BigDecimal price,
      LocalDateTime createdAt) {

    // 페이징시 최대 출력 갯수와 정렬조건 설정
    PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Direction.DESC, "id"));

    return gameRepository.searchGames(pageRequest, title, category, price, createdAt);
  }

  // 게임 수정
  @Transactional
  public CreatedGameResponseDto updateGame(Long gameId, Long userId,
      UpdatedGameRequestDto requestDto, String imageUrl) {

    Game game = gameRepository.findByIdOrElseThrow(gameId);

    // 현재 접속한 유저가 게임을 생성한 유저가 맞는지 비교
    if (!game.getUser().getId().equals(userId)) {
      throw new NoAuthorizedException(GameErrorCode.DOES_NOT_MATCH);
    }

    game.updateGame(requestDto, imageUrl);
    gameRepository.save(game);
    return CreatedGameResponseDto.toDto(game);
  }

  @Transactional
  public void deleteGame(Long gameId, Long userId) {

    Game game = gameRepository.findByIdOrElseThrow(gameId);

    // 현재 접속한 유저가 게임을 생성한 유저가 맞는지 비교
    if (!game.getUser().getId().equals(userId)) {
      throw new NoAuthorizedException(GameErrorCode.DOES_NOT_MATCH);
    }

    game.deleteGame();

    // 삭제하는 게임 id를 가지고 있는 게임 태그를 hard delete
    List<GameTag> gameTagList = gameTagRepository.findGameTagsByGameId(gameId);
    gameTagRepository.deleteAll(gameTagList);

    // 삭제하는 게임 id를 가지고 있는 라이브러리를 hard delete
    List<Library> libraryList = libraryRepository.findLibraryByGameId(gameId);
    libraryRepository.deleteAll(libraryList);


    gameRepository.save(game);
  }
}
