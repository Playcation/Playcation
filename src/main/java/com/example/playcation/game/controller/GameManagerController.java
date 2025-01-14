package com.example.playcation.game.controller;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.game.dto.ManagerGameDetailResponseDto;
import com.example.playcation.game.service.GameManagerService;
import com.example.playcation.util.JWTUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/games")
public class GameManagerController {

  private final GameManagerService gameService;
  private final JWTUtil jwtUtil;

  /**
   * 현재 유저가 판매중인 게임 목록 반환
   */
  @GetMapping
  public ResponseEntity<List<CartGameResponseDto>> findSellingGames(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {

    List<CartGameResponseDto> dtos = gameService.findSellingGames(
        jwtUtil.findUserByToken(authorizationHeader));

    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  /**
   * 게임의 매니저 페이지 상세 정보를 조회
   *
   * @param gameId 조회할 게임 식별자
   */
  @GetMapping("/{gameId}")
  public ResponseEntity<ManagerGameDetailResponseDto> findSellingGameDetail(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable Long gameId
  ) {

    ManagerGameDetailResponseDto dto = gameService.findSellingGameDetail(
        jwtUtil.findUserByToken(authorizationHeader), gameId);

    return new ResponseEntity<>(dto, HttpStatus.OK);
  }
}
