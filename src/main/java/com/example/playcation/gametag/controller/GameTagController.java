package com.example.playcation.gametag.controller;

import com.example.playcation.gametag.dto.GameTagRequestDto;
import com.example.playcation.gametag.dto.GameTagResponseDto;
import com.example.playcation.gametag.service.GameTagService;
import com.example.playcation.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game_tags")
@RequiredArgsConstructor
public class GameTagController {

  private final GameTagService gameTagService;
  private final JWTUtil jwtUtil;

  @PostMapping
  public ResponseEntity<GameTagResponseDto> createGameTag(@RequestBody GameTagRequestDto requestDto) {
    GameTagResponseDto responseDto = gameTagService.createGameTag(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @PatchMapping("/{gameTagId}")
  public ResponseEntity<GameTagResponseDto> updateGameTag(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long gameTagId, @RequestBody GameTagRequestDto requestDto) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    GameTagResponseDto responseDto = gameTagService.updateGameTag(userId, gameTagId, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @DeleteMapping("/{gameTagId}")
  public ResponseEntity<String> deleteGameTag(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long gameTagId) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    gameTagService.deleteGame(userId, gameTagId);
    return new ResponseEntity<>("삭제 완료되었습니다", HttpStatus.OK);
  }
}
