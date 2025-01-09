package com.example.playcation.library.controller;

import com.example.playcation.gametag.dto.GameListResponseDto;
import com.example.playcation.library.dto.LibraryGameResponseDto;
import com.example.playcation.library.dto.LibraryRequestDto;
import com.example.playcation.library.dto.LibraryResponseDto;
import com.example.playcation.library.service.LibraryService;
import com.example.playcation.util.JWTUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nonapi.io.github.classgraph.utils.LogNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/libraries")
public class LibraryController {

  private final LibraryService libraryService;
  private final JWTUtil jwtUtil;

  @PostMapping
  public ResponseEntity<LibraryResponseDto> createLibrary(@RequestHeader("Authorization") String authorizationHeader,
      @RequestBody LibraryRequestDto requestDto) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    LibraryResponseDto responseDto = libraryService.createLibrary(requestDto, userId);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping("/{libraryId}")
  public ResponseEntity<LibraryResponseDto> findLibrary(@PathVariable Long libraryId) {
    LibraryResponseDto responseDto = libraryService.findLibrary(libraryId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 다건 조회(라이브러리)
  @GetMapping("/my-games")
  public ResponseEntity<List<LibraryGameResponseDto>> findLibraryList(@RequestHeader("Authorization") String authorizationHeader,
      @RequestParam(defaultValue = "0") int page) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    List<LibraryGameResponseDto> responseDto = libraryService.findLibraryList(page, userId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
