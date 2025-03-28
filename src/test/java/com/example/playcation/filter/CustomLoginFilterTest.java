package com.example.playcation.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.user.entity.CustomUserDetails;
import com.example.playcation.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.mapping.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class CustomLoginFilterTest {

  private AuthenticationManager authenticationManager;

  private JWTUtil jwtUtil;

  private CustomLoginFilter customLoginFilter;

  @BeforeEach
  void setUp() {
    authenticationManager = mock(AuthenticationManager.class);
    jwtUtil = mock(JWTUtil.class);
    customLoginFilter = new CustomLoginFilter(authenticationManager, jwtUtil);
  }

  @Test
  @DisplayName("✅ 올바른 로그인 요청이 주어지면 인증이 정상적으로 수행되어야 한다.")
  void shouldAuthenticateUserSuccessfully() throws Exception {
    // Given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(new ObjectMapper().writeValueAsBytes(Map.of(
        "email", "user@example.com",
        "password", "password123"
    )));

    MockHttpServletResponse response = new MockHttpServletResponse();

    Authentication auth = new UsernamePasswordAuthenticationToken("user@example.com", "password123");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

    // When
    Authentication result = customLoginFilter.attemptAuthentication(request, response);

    // Then
    assertThat(result).isNotNull();
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @DisplayName("❌ 잘못된 요청이 들어오면 RuntimeException이 발생해야 한다.")
  void shouldThrowExceptionWhenInvalidRequest() {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    try {
      when(request.getInputStream()).thenThrow(new RuntimeException("Invalid Request"));

      // When & Then
      org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
          () -> customLoginFilter.attemptAuthentication(request, response));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("✅ 로그인 성공 시 JWT 토큰이 응답으로 반환되어야 한다.")
  void shouldReturnTokensOnSuccessfulAuthentication() throws Exception {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);
    PrintWriter writer = mock(PrintWriter.class);
    Authentication authentication = mock(Authentication.class);
    CustomUserDetails customUserDetails = mock(CustomUserDetails.class);

    doReturn(customUserDetails).when(authentication).getPrincipal();
    doReturn(1L).when(customUserDetails).getUserId();
    doReturn(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
        .when(authentication).getAuthorities();
    when(jwtUtil.generateTokens("1", "ROLE_USER")).thenReturn(new String[]{"accessToken", "refreshToken"});
    when(response.getWriter()).thenReturn(writer);
    when(jwtUtil.createCookie(any(), any())).thenReturn(new Cookie("refresh", "refreshToken"));

    // When
    customLoginFilter.successfulAuthentication(request, response, chain, authentication);

    // Then
    verify(response).setStatus(HttpServletResponse.SC_OK);
    verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    verify(response).addCookie(any(Cookie.class));
    verify(writer).write("{\"token\" : \"accessToken\"}");
  }

  @Test
  @DisplayName("✅ 로그인 성공 시 RefreshToken이 쿠키에 저장되어야 한다.")
  void shouldSetRefreshTokenInCookieOnSuccess() throws Exception {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);
    CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
    Authentication authentication = mock(Authentication.class);

    doReturn(customUserDetails).when(authentication).getPrincipal();
    doReturn(1L).when(customUserDetails).getUserId();
    doReturn(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
        .when(authentication).getAuthorities();

    when(jwtUtil.generateTokens("1", "ROLE_USER")).thenReturn(new String[]{"accessToken", "refreshToken"});
    when(jwtUtil.createCookie(any(), any())).thenReturn(new Cookie("refresh", "refreshToken"));

    // When
    customLoginFilter.successfulAuthentication(request, response, chain, authentication);

    // Then
    Cookie refreshTokenCookie = response.getCookie(TokenSettings.REFRESH_TOKEN_CATEGORY);
    assertThat(refreshTokenCookie).isNotNull();
    assertThat(refreshTokenCookie.getValue()).isEqualTo("refreshToken");
  }
}