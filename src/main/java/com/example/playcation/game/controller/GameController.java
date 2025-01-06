package com.example.playcation.game.controller;

import com.example.playcation.game.Dto.CreatedGameRequestDto;
import com.example.playcation.game.Dto.CreatedGameResponseDto;
import com.example.playcation.game.Dto.PageGameResponseDto;
import com.example.playcation.game.service.GameService;
import com.example.playcation.util.JwtTokenProvider;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class GameController {

  private final GameService gameService;
  private final JwtTokenProvider jwtTokenProvider;

  // 게임 생성 컨트롤러
  @PostMapping
  public ResponseEntity<CreatedGameResponseDto> createdCard(@RequestHeader("Authorization") String authorizationHeader, CreatedGameRequestDto requestDto) {
    String token = authorizationHeader.replace("Bearer ", "").trim();
    Long id = Long.parseLong(jwtTokenProvider.getUserId(token));
    CreatedGameResponseDto responseDto = gameService.createdGame(id, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  // 게입 단건 조회 컨트롤러
  @GetMapping("/{gameId}")
  public ResponseEntity<CreatedGameResponseDto> findGame(@PathVariable Long gameId) {
    CreatedGameResponseDto responseDto = gameService.findGameById(gameId);
    return new ResponseEntity<>(responseDto,HttpStatus.OK);
  }

  //게임 다건 조회 컨트롤러
  @GetMapping
  public ResponseEntity<PageGameResponseDto> findGamesAndPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) BigDecimal price,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime createdAt
      ) {
    PageGameResponseDto games = gameService.searchGames(page, title, category, price, createdAt);
    return new ResponseEntity<>(games, HttpStatus.OK);
  }
}
