package com.example.playcation.token.service;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.TokenErrorCode;
import com.example.playcation.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final RedisTemplate<String, String> redisTemplate;
  private final JWTUtil jwtUtil;

  public String[] createNewToken(HttpServletRequest request) {
    String refresh = extractRefreshToken(request);
    validateRefreshToken(refresh);

    String userId = jwtUtil.getUserId(refresh);
    String auth = jwtUtil.getAuth(refresh);

    // Redis에서 기존 Refresh 토큰 조회 및 검증
    String storedRefresh = redisTemplate.opsForValue().get(userId);
    if (storedRefresh == null || !storedRefresh.equals(refresh)) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    // 새로운 액세스 & 리프레시 토큰 생성
    String[] tokens = generateTokens(userId, auth);

    // Redis에 새 Refresh 토큰 저장
    storeNewRefreshToken(userId, tokens[1]);

    return tokens;
  }

  // HTTP 요청에서 Refresh 토큰 추출
  private String extractRefreshToken(HttpServletRequest request) {
    for (Cookie cookie : request.getCookies()) {
      if (cookie.getName().equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
        return cookie.getValue();
      }
    }
    throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
  }

  // Refresh 토큰 유효성 검사
  private void validateRefreshToken(String refresh) {
    try {
      jwtUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }
    if (!jwtUtil.getCategory(refresh).equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }
  }

  // JWT 액세스/리프레시 토큰 생성
  private String[] generateTokens(String userId, String auth) {
    String access = TokenSettings.TOKEN_TYPE + jwtUtil.createJwt(TokenSettings.ACCESS_TOKEN_CATEGORY, userId, auth, TokenSettings.ACCESS_TOKEN_EXPIRATION);
    String refresh = jwtUtil.createJwt(TokenSettings.REFRESH_TOKEN_CATEGORY, userId, auth, TokenSettings.REFRESH_TOKEN_EXPIRATION);
    return new String[]{access, refresh};
  }

  // Redis에 새로운 Refresh 토큰 저장
  private void storeNewRefreshToken(String userId, String refreshToken) {
    redisTemplate.delete(userId);
    redisTemplate.opsForValue().set(userId, refreshToken, Duration.ofMillis(TokenSettings.REFRESH_TOKEN_EXPIRATION));
  }
}