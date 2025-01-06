package com.example.playcation.game.controller;

import com.example.playcation.game.Dto.CreatedGameRequestDto;
import com.example.playcation.game.Dto.CreatedGameResponseDto;
import com.example.playcation.game.service.GameService;
import com.example.playcation.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class GameController {

  private final GameService gameService;
  private JwtTokenProvider jwtTokenProvider;

  @PostMapping
  public ResponseEntity<CreatedGameResponseDto> createdCard(@RequestHeader("Authorization") String authorizationHeader, CreatedGameRequestDto requestDto) {
    String token = authorizationHeader.replace("Bearer ", "").trim();
    Long id = Long.parseLong(jwtTokenProvider.getUserId(token));
    CreatedGameResponseDto responseDto = gameService.createdGame(id, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

}
