package com.example.playcation.filter;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.TokenErrorCode;
import com.example.playcation.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

  private final RedisTemplate<String, String> redisTemplate;
  private final JWTUtil jwtUtil;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
  }

  private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

    // 엔드포인트가 logout 인지 확인
    String requestUri = request.getRequestURI();
    if (!requestUri.matches("^\\/logout$")) {
      filterChain.doFilter(request, response);
      return;
    }
    // POST 요청인지 확인
    String requestMethod = request.getMethod();
    if (!requestMethod.equals("POST")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 쿠키에서 refresh 토큰 가져오기
    String refresh = null;
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
        refresh = cookie.getValue();
      }
    }

    // Refresh 토큰이 null인지 확인
    if (refresh == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // 만료 확인 / 토큰 발급자 확인
    try {
      jwtUtil.isExpired(refresh);
      jwtUtil.isIssuer(refresh);
    } catch (ExpiredJwtException e) {
      //response status code
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
    String category = jwtUtil.getCategory(refresh);
    if (!category.equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
      //response status code
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String userId = jwtUtil.getUserId(refresh);

    // Redis에서 Refresh Token 조회
    String storedRefresh = redisTemplate.opsForValue().get(userId);
    if (storedRefresh == null || !storedRefresh.equals(refresh)) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    //로그아웃 진행
    //Refresh 토큰 DB에서 제거
    redisTemplate.delete(userId);

    response.setHeader(TokenSettings.ACCESS_TOKEN_CATEGORY, "");

    //Refresh 토큰 Cookie 값 0
    Cookie cookie = new Cookie(TokenSettings.REFRESH_TOKEN_CATEGORY, null);
    cookie.setMaxAge(0);
    cookie.setPath("/");

    response.addCookie(cookie);
    response.setStatus(HttpServletResponse.SC_OK);
  }
}
