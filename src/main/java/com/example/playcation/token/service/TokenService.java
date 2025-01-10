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

    String refresh = null;
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
        refresh = cookie.getValue();
      }
    }

    if (refresh == null) {
      // 리플레시 토큰이 없을 때
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    // 토큰 만료 확인
    try {
      jwtUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
      // 리플레시 토큰이 만료되었을 때
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
    String category = jwtUtil.getCategory(refresh);
    if (!category.equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
      // 리플레시 토큰이 아닐 때
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    String userId = jwtUtil.getUserId(refresh);
    String auth = jwtUtil.getAuth(refresh);

    // Redis에서 Refresh Token 조회
    String storedRefresh = redisTemplate.opsForValue().get(userId);
    if (storedRefresh == null || !storedRefresh.equals(refresh)) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    // JWT 토큰 생성
    String newAccess = TokenSettings.TOKEN_TYPE + jwtUtil.createJwt(TokenSettings.ACCESS_TOKEN_CATEGORY, userId, auth, TokenSettings.ACCESS_TOKEN_EXPIRATION);
    String newRefresh = jwtUtil.createJwt(TokenSettings.REFRESH_TOKEN_CATEGORY, userId, auth, TokenSettings.REFRESH_TOKEN_EXPIRATION);

    // Redis에서 기존 Refresh 토큰 삭제 후 새 토큰 저장
    redisTemplate.delete(userId);
    addRefreshEntity(userId, newRefresh, TokenSettings.REFRESH_TOKEN_EXPIRATION);


    return new String[] {newAccess, newRefresh};
  }

  private void addRefreshEntity(String userId, String refresh, Long expiredMs) {

    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(userId, refresh, Duration.ofMillis(expiredMs));  // Redis에 저장
  }
}
