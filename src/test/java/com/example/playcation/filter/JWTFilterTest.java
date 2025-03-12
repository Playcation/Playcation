package com.example.playcation.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.playcation.common.TestController;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.token.service.TokenService;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class JWTFilterTest {

  private MockMvc mockMvc;

  @InjectMocks
  private JWTFilter jwtFilter;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TokenService tokenService;

  @Mock
  private JWTUtil jwtUtil;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
        .addFilter(jwtFilter)
        .build();
  }

  @Test
  @DisplayName("필터 정상 작동 테스트")
  void doFilterInternal() throws Exception {
    // Given
    String validToken = "validAccessToken";
    when(jwtUtil.isExpired(validToken)).thenReturn(false);
    when(jwtUtil.isIssuer(validToken)).thenReturn(true);
    when(jwtUtil.getCategory(validToken)).thenReturn(TokenSettings.ACCESS_TOKEN_CATEGORY);
    when(jwtUtil.getUserId(validToken)).thenReturn("1");

    // When
    User mockUser = new User();
    when(userRepository.findByIdOrElseThrow(1L)).thenReturn(mockUser);

    // Then
    mockMvc.perform(requestWithJWT(validToken))
        .andExpect(status().isOk());
    assert SecurityContextHolder.getContext().getAuthentication() != null;
  }

  @Test
  @DisplayName("JWT가 없는 요청 필터 통과 테스트")
  void shouldAllowRequestsWithoutJWT() throws Exception {
    mockMvc.perform(requestWithoutJWT())
        .andExpect(status().isOk()); // 필터에서 통과하여 정상 응답
  }

  @Test
  @DisplayName("만료된 JWT 토큰에서 토큰 재발급 확인")
  void shouldRefreshAccessTokenWhenExpired() throws Exception {
    String expiredAccessToken = "expiredAccessToken";
    String validRefreshToken = "validRefreshToken";
    String newAccessToken = "\"newAccessToken\"";
    String newRefreshToken = "newRefreshToken";

    // Access Token 만료 처리
    doThrow(ExpiredJwtException.class).when(jwtUtil).isExpired(expiredAccessToken);

    // Refresh Token 검증 및 재발급
    when(jwtUtil.isExpired(validRefreshToken)).thenReturn(false);
    when(jwtUtil.getUserId(validRefreshToken)).thenReturn("1");
    when(tokenService.createNewToken(any())).thenReturn(new String[]{newAccessToken, newRefreshToken});

    mockMvc.perform(requestWithExpiredToken(expiredAccessToken, validRefreshToken))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"token\" : \"" + newAccessToken + "\"}"));
  }

  @Test
  @DisplayName("Token이 모두 무효일때 실패 테스트")
  void shouldReturnUnauthorizedWhenRefreshTokenIsInvalid() throws Exception {
    String expiredAccessToken = "expiredAccessToken";
    String expiredRefreshToken = "expiredRefreshToken";

    // Access Token 만료 처리
    doThrow(ExpiredJwtException.class).when(jwtUtil).isExpired(expiredAccessToken);

    // Refresh Token도 만료 처리
    doThrow(InvalidInputException.class).when(jwtUtil).isExpired(expiredRefreshToken);

    mockMvc.perform(requestWithExpiredToken(expiredAccessToken, expiredRefreshToken))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("잘못된 리플레시 토큰입니다."));
  }

  // ---- [ 요청 생성 메서드들 ] ----

  private RequestBuilder requestWithoutJWT() {
    return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/test");
  }

  private RequestBuilder requestWithJWT(String accessToken) {
    return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/test")
        .header(TokenSettings.ACCESS_TOKEN_CATEGORY, "Bearer " + accessToken);
  }

  private RequestBuilder requestWithExpiredToken(String expiredAccessToken, String refreshToken) {
    return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/test")
        .header(TokenSettings.ACCESS_TOKEN_CATEGORY, "Bearer " + expiredAccessToken)
        .cookie(new Cookie(TokenSettings.REFRESH_TOKEN_CATEGORY, refreshToken));
  }
}