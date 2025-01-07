package com.example.playcation.gametag.controller;

import com.example.playcation.gametag.dto.GameListResponseDto;
import com.example.playcation.gametag.dto.GameTagRequestDto;
import com.example.playcation.gametag.dto.GameTagResponseDto;
import com.example.playcation.gametag.service.GameTagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.LocalizedMessageFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gameTags")
@RequiredArgsConstructor
public class GameTagController {

  private final GameTagService gameTagService;

  @PostMapping
  public ResponseEntity<GameTagResponseDto> createdGameTag(@RequestBody GameTagRequestDto requestDto) {
    GameTagResponseDto responseDto = gameTagService.createdGameTag(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<GameListResponseDto> findGameTag(
      @RequestParam(required = false) int page,
      @RequestParam Long tagId) {
    GameListResponseDto responseDto = gameTagService.findGameTag(page, tagId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
