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
    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      doLogoutFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    } else {
      chain.doFilter(request, response);
    }
  }

  private void doLogoutFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    if (!isLogoutRequest(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String refreshToken = extractRefreshToken(request);
    if (refreshToken == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    if (!validateToken(refreshToken, response)) {
      return;
    }

    performLogout(refreshToken, response);
  }

  private boolean isLogoutRequest(HttpServletRequest request) {
    return "/logout".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod());
  }

  private String extractRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;

    for (Cookie cookie : cookies) {
      if (TokenSettings.REFRESH_TOKEN_CATEGORY.equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  private boolean validateToken(String token, HttpServletResponse response) throws IOException {
    try {
      jwtUtil.isExpired(token);
      jwtUtil.isIssuer(token);
    } catch (ExpiredJwtException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return false;
    }

    if (!TokenSettings.REFRESH_TOKEN_CATEGORY.equals(jwtUtil.getCategory(token))) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return false;
    }
    return true;
  }

  private void performLogout(String refreshToken, HttpServletResponse response) {
    String userId = jwtUtil.getUserId(refreshToken);
    String storedRefresh = redisTemplate.opsForValue().get(userId);

    if (storedRefresh == null || !storedRefresh.equals(refreshToken)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    redisTemplate.delete(userId);
    clearCookies(response);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private void clearCookies(HttpServletResponse response) {
    Cookie cookie = new Cookie(TokenSettings.REFRESH_TOKEN_CATEGORY, null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}
