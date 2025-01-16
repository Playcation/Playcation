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
   * Refresh Token을 사용한 Access Token 갱신
   */
  @PostMapping("/token/refresh")
  public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
    try {
      // Refresh Token 추출
      String refreshToken = extractRefreshToken(request);
      jwtUtil.verifyRefreshTokenFormat(refreshToken);

      // 사용자 ID 추출 및 Redis에서 검증
      String userId = jwtUtil.getUserId(refreshToken);
      if (!jwtUtil.checkRefreshTokenMatch(userId, refreshToken)) {
        throw new InvalidInputException(TokenErrorCode.REFRESH_TOKEN_MISS_MATCH);
      }

      // 새로운 Access Token 생성
      String userRole = jwtUtil.getAuth(refreshToken);
      String newAccessToken = jwtUtil.generateTokens(userId, userRole)[0];

    } catch (Exception e) {
    }
  }

  /**
   * 로그아웃 - Refresh Token 삭제
   */
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = extractRefreshToken(request);
    if (refreshToken != null) {
      String userId = jwtUtil.getUserId(refreshToken);
      jwtUtil.deleteRefreshTokenFromRedis(userId);
    }

    // Refresh Token 쿠키 삭제
    Cookie emptyCookie = jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, null);
    emptyCookie.setMaxAge(0);
    response.addCookie(emptyCookie);

    return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
  }

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

  /**
   * Refresh Token 추출 메서드
   */
  private String extractRefreshToken(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (TokenSettings.REFRESH_TOKEN_CATEGORY.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
