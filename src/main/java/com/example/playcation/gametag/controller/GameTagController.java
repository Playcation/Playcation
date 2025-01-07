package com.example.playcation.gametag.controller;

import com.example.playcation.gametag.dto.GameListResponseDto;
import com.example.playcation.gametag.dto.GameTagRequestDto;
import com.example.playcation.gametag.dto.GameTagResponseDto;
import com.example.playcation.gametag.service.GameTagService;
import com.example.playcation.util.TokenUtil;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gametags")
@RequiredArgsConstructor
public class GameTagController {

  private final GameTagService gameTagService;
  private final TokenUtil tokenUtil;

  @PostMapping
  public ResponseEntity<GameTagResponseDto> createdGameTag(@RequestBody GameTagRequestDto requestDto) {
    GameTagResponseDto responseDto = gameTagService.createdGameTag(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @PatchMapping("/{gameTagId}")
  public ResponseEntity<GameTagResponseDto> updatedGameTag(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long gameTagId, @RequestBody GameTagRequestDto requestDto) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    GameTagResponseDto responseDto = gameTagService.updatedGameTag(userId, gameTagId, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @DeleteMapping("/{gameTagId}")
  public ResponseEntity<String> deletedGameTag(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long gameTagId) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    gameTagService.deletedGame(userId, gameTagId);
    return new ResponseEntity<>("삭제 완료되었습니다", HttpStatus.OK);
  }
}
