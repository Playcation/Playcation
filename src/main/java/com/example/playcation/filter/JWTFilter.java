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
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    // 헤더에서 Authorization키에 담긴 토큰을 꺼냄
    String accessToken = request.getHeader(TokenSettings.ACCESS_TOKEN_CATEGORY);
   if(accessToken != null) {
     accessToken = accessToken.replace(TokenSettings.TOKEN_TYPE, "").trim();
   }

    // 토큰이 없다면 다음 필터로 넘김
    if (accessToken == null) {

      filterChain.doFilter(request, response);

      return;
    }

    // 토큰 만료 여부 확인, 토큰의 발급자 확인 만료시 다음 필터로 넘기지 않음
    try {
      jwtUtil.isExpired(accessToken);
      jwtUtil.isIssuer(accessToken);
    } catch (ExpiredJwtException e) {

      //response body
      PrintWriter writer = response.getWriter();
      writer.print("토큰이 만료되었습니다.");

      //response status code
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    // 토큰이 Authorization인지 확인 (발급시 페이로드에 명시)
    String category = jwtUtil.getCategory(accessToken);

    if (!category.equals(TokenSettings.ACCESS_TOKEN_CATEGORY)) {

      //response body
      PrintWriter writer = response.getWriter();
      writer.print("잘못된 토큰입니다.");

      //response status code
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    Long userId = Long.parseLong(jwtUtil.getUserId(accessToken));

    User user = userRepository.findByIdOrElseThrow(userId);

    CustomUserDetails customUserDetails = new CustomUserDetails(user);

    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
