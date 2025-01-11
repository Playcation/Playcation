package com.example.playcation.filter;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.user.entity.CustomUserDetails;
import com.example.playcation.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final RedisTemplate<String, String> redisTemplate;
  private final JWTUtil jwtUtil;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, String> loginData = objectMapper.readValue(request.getInputStream(), Map.class);
      String email = loginData.get("email");
      String password = loginData.get("password");

      return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password, null));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = customUserDetails.getUserId().toString();
    String role = authentication.getAuthorities().iterator().next().getAuthority();

    // JWT 토큰 생성
    String[] tokens = generateTokens(userId, role);

    // Refresh 토큰 저장
    storeRefreshToken(userId, tokens[1]);

    response.setHeader(TokenSettings.ACCESS_TOKEN_CATEGORY, tokens[0]);
    response.addCookie(jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, tokens[1]));
    response.setStatus(HttpStatus.OK.value());
  }

  // JWT 액세스/리프레시 토큰 생성 메서드
  private String[] generateTokens(String userId, String role) {
    String access = TokenSettings.TOKEN_TYPE + jwtUtil.createJwt(TokenSettings.ACCESS_TOKEN_CATEGORY, userId, role, TokenSettings.ACCESS_TOKEN_EXPIRATION);
    String refresh = jwtUtil.createJwt(TokenSettings.REFRESH_TOKEN_CATEGORY, userId, role, TokenSettings.REFRESH_TOKEN_EXPIRATION);
    return new String[]{access, refresh};
  }

  // Redis에 Refresh 토큰 저장 메서드
  private void storeRefreshToken(String userId, String refreshToken) {
    redisTemplate.opsForValue().set(userId, refreshToken, Duration.ofMillis(TokenSettings.REFRESH_TOKEN_EXPIRATION));
  }
}