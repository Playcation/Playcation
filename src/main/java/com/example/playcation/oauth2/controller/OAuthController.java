package com.example.playcation.oauth2.controller;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.TokenErrorCode;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.service.UserService;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {

  private final JWTUtil jwtUtil;
  private final UserService userService;

  /**
   * 사용자 정보 조회
   */
  @GetMapping("/me")
  public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String accessToken) {
    try {
      Long userId = jwtUtil.findUserByToken(accessToken);
      UserResponseDto user = userService.findUser(userId);
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    }
  }
}
