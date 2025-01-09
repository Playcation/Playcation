package com.example.playcation.gametag.service;

import com.example.playcation.exception.GameTagErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.gametag.dto.GameListResponseDto;
import com.example.playcation.gametag.dto.GameTagRequestDto;
import com.example.playcation.gametag.dto.GameTagResponseDto;
import com.example.playcation.gametag.entity.GameTag;
import com.example.playcation.gametag.repository.GameTagRepository;
import com.example.playcation.tag.entity.Tag;
import com.example.playcation.tag.repository.TagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameTagService {

  private final GameTagRepository gameTagRepository;
  private final TagRepository tagRepository;
  private final GameRepository gameRepository;


  public GameTagResponseDto createGameTag(GameTagRequestDto requestDto) {

    Tag tag = tagRepository.findByIdOrElseThrow(requestDto.getTagId());

    Game game = gameRepository.findByIdOrElseThrow(requestDto.getGameId());

    GameTag gameTag = GameTag.builder()
        .tag(tag)
        .game(game)
        .build();

    gameTagRepository.save(gameTag);

    return GameTagResponseDto.toDto(gameTag);
  }


  public GameListResponseDto findGameTagByTag(int page, Long tagId) {

    PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Direction.DESC, "id"));

    Tag tag = tagRepository.findByIdOrElseThrow(tagId);

    List<GameTag> gameTagList = gameTagRepository.findGameTagByTag(pageRequest, tag).stream().toList();

    List<Game> gameList = new ArrayList<>();
    for (GameTag gameTag : gameTagList) {
      gameList.add(gameTag.getGame());
    }

    return new GameListResponseDto(gameList);
  }

  // 게임 태그 수정(게임 id는 수정 불가)
  public GameTagResponseDto updateGameTag(Long userId, Long gameTagId, GameTagRequestDto requestDto) {

    GameTag gameTag = gameTagRepository.findByIdOrElseThrow(gameTagId);

    if (!gameTag.getGame().getUser().getId().equals(userId)) {
      throw new NotFoundException(GameTagErrorCode.GAME_TAG_NOT_FOUND);
    }

    Tag tag = tagRepository.findByIdOrElseThrow(requestDto.getTagId());

    gameTag.updateGameTag(tag);

    gameTagRepository.save(gameTag);

    return GameTagResponseDto.toDto(gameTag);
  }

  public void deleteGame(Long userId, Long gameTagId) {

    GameTag gameTag = gameTagRepository.findByIdOrElseThrow(gameTagId);

    if (!gameTag.getGame().getUser().getId().equals(userId)) {
      throw new NotFoundException(GameTagErrorCode.GAME_TAG_NOT_FOUND);
    }

    gameTagRepository.delete(gameTag);
  }
}
