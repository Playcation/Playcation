package com.example.playcation.filter;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.enums.Role;
import com.example.playcation.user.entity.CustomUserDetails;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final UserRepository userRepository;
  private final JWTUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestUri = request.getRequestURI();
    if (isLoginRequest(requestUri)) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = extractToken(request);
    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      validateToken(accessToken);
      authenticateUser(accessToken);
    } catch (ExpiredJwtException e) {
      sendErrorResponse(response, "토큰이 만료되었습니다.");
      return;
    } catch (Exception e) {
      sendErrorResponse(response, "잘못된 토큰입니다.");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isLoginRequest(String uri) {
    return uri.matches("^/login(?:/.*)?$") || uri.matches("^/oauth2(?:/.*)?$");
  }

  private String extractToken(HttpServletRequest request) {
    String token = request.getHeader(TokenSettings.ACCESS_TOKEN_CATEGORY);
    if (token != null && token.startsWith(TokenSettings.TOKEN_TYPE)) {
      return token.substring(TokenSettings.TOKEN_TYPE.length()).trim();
    }
    return null;
  }

  private void validateToken(String token) {
    jwtUtil.isExpired(token);
    jwtUtil.isIssuer(token);
    String category = jwtUtil.getCategory(token);
    if (!TokenSettings.ACCESS_TOKEN_CATEGORY.equals(category)) {
      throw new IllegalArgumentException("잘못된 토큰입니다: 카테고리 불일치");
    }
  }

  private void authenticateUser(String token) {
    Long userId = Long.parseLong(jwtUtil.getUserId(token));
    User user = userRepository.findByIdOrElseThrow(userId);
    CustomUserDetails userDetails = new CustomUserDetails(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    try (PrintWriter writer = response.getWriter()) {
      writer.print(message);
    }
  }
}
