package com.example.playcation.oauth2.handler;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.oauth2.dto.OAuth2UserDto;
import com.example.playcation.token.entity.RefreshToken;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final JWTUtil jwtUtil;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

    // 사용자 정보 가져오기
    OAuth2UserDto userDetails = (OAuth2UserDto) authentication.getPrincipal();
    String email = userDetails.getEmail();
    User user = userRepository.findByEmailOrElseThrow(email);

    // 권한(role) 가져오기
    String role = authentication.getAuthorities()
        .stream()
        .findFirst()
        .map(GrantedAuthority::getAuthority)
        .orElse("USER");

    // 액세스 & 리프레시 토큰 생성
    String[] tokens = jwtUtil.generateTokens(user.getId().toString(), role);
    String accessToken = tokens[0];
    String refreshToken = tokens[1];

    // 응답 설정 (헤더 & 쿠키)
    setTokenResponse(response, accessToken, refreshToken);

    // 리디렉트 처리
    response.sendRedirect("http://localhost:8080/my");
  }

  /**
   * 응답 헤더 및 쿠키 설정
   */
  private void setTokenResponse(HttpServletResponse response, String accessToken, String refreshToken) {
    response.setHeader(TokenSettings.ACCESS_TOKEN_CATEGORY, accessToken);
    response.addCookie(jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, refreshToken));
  }
}
