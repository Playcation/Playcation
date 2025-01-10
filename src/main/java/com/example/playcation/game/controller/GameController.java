package com.example.playcation.game.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.game.dto.CreatedGameRequestDto;
import com.example.playcation.game.dto.CreatedGameResponseDto;
import com.example.playcation.game.dto.UpdatedGameRequestDto;
import com.example.playcation.game.service.GameService;
import com.example.playcation.gametag.dto.GameListResponseDto;
import com.example.playcation.gametag.service.GameTagService;
import com.example.playcation.library.service.LibraryService;
import com.example.playcation.util.JWTUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

  private final GameService gameService;
  private final GameTagService gameTagService;
  private final LibraryService libraryService;
  private final JWTUtil jwtUtil;

  // 게임 생성 컨트롤러
  @PostMapping
  public ResponseEntity<CreatedGameResponseDto> createGame(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestBody CreatedGameRequestDto requestDto) {
    Long id = jwtUtil.findUserByToken(authorizationHeader);
    CreatedGameResponseDto responseDto = gameService.createGame(id, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  // 게입 단건 조회 컨트롤러
  @GetMapping("/{gameId}")
  public ResponseEntity<CreatedGameResponseDto> findGame(@PathVariable Long gameId) {
    CreatedGameResponseDto responseDto = gameService.findGameById(gameId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  //게임 다건 조회 컨트롤러
  @GetMapping
  public ResponseEntity<PagingDto<CreatedGameResponseDto>> findGamesAndPaging(
      // 조회하고 싶은 페이지(미입력시 자동으로 첫 페이지가 출력)
      @RequestParam(defaultValue = "0") int page,
      // 검색 조건(tag는 게임에서 찾을 수 없음으로 따로 제작)
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) BigDecimal price,
      @RequestParam(required = false) LocalDate createdAt
  ) {
    PagingDto<CreatedGameResponseDto> games = gameService.searchGames(page, title, category, price,
        createdAt.atStartOfDay());
    return new ResponseEntity<>(games, HttpStatus.OK);
  }

  // 다건 조회(태그)
  @GetMapping("/tag")
  public ResponseEntity<GameListResponseDto> findGameTag(
      @RequestParam(required = false) int page,
      @RequestParam Long tagId) {
    GameListResponseDto responseDto = gameTagService.findGameTagByTag(page, tagId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }


  // 게임 수정 컨트롤러
  @PatchMapping("/{gameId}")
  public ResponseEntity<CreatedGameResponseDto> updateGame(
      @PathVariable Long gameId,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestPart("dto") UpdatedGameRequestDto requestDto,
      @RequestPart(value = "file", required = false) String imageUrl) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    CreatedGameResponseDto responseDto = gameService.updateGame(gameId, userId, requestDto,
        imageUrl);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @DeleteMapping("/{gameId}")
  public ResponseEntity<String> deleteGame(@PathVariable Long gameId,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);

    gameService.deleteGame(gameId, userId);
    return new ResponseEntity<>("삭제되었습니다", HttpStatus.OK);
  }


}
