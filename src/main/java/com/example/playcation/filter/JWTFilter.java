package com.example.playcation.filter;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.enums.Role;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.TokenErrorCode;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.token.service.TokenService;
import com.example.playcation.user.entity.CustomUserDetails;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 및 유효성 검사를 수행하는 필터
 *
 * <p>- Spring Security의 OncePerRequestFilter를 상속받아 모든 요청에서 한 번만 실행됨</p>
 * <p>- 로그인 요청은 필터링하지 않음</p>
 * <p>- JWT 토큰을 검증하고, 유효한 경우 사용자 정보를 SecurityContext에 저장</p>
 */
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final UserRepository userRepository;
  private final TokenService tokenService;
  private final JWTUtil jwtUtil;

  /**
   * 모든 요청에서 JWT 인증을 수행하는 메서드
   *
   * @param request     HTTP 요청 객체
   * @param response    HTTP 응답 객체
   * @param filterChain 필터 체인 객체
   * @throws ServletException, IOException
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String requestUri = request.getRequestURI();
    if (isLoginRequest(requestUri) || isSinginRequest(requestUri)) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = request.getHeader(TokenSettings.ACCESS_TOKEN_CATEGORY);
    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }
    accessToken = accessToken.replace("Bearer ", "");

    try {
      authenticateUser(accessToken);
      validateToken(accessToken);
    } catch (ExpiredJwtException e) {
      // 쿠키에서 리플레시 토큰 확인하기 -> 유효하면 트큰 재발급 / 아니면 이대로 진행
      String refreshToken = Arrays.stream(request.getCookies())
          .filter(cookie -> TokenSettings.REFRESH_TOKEN_CATEGORY.equals(
              cookie.getName())) // 이름이 일치하는 쿠키 필터링
          .map(Cookie::getValue) // 쿠키 값을 추출
          .findFirst() // 첫 번째 값 가져오기
          .orElse(null); // 없으면 null 반환
      try {
        authenticateUser(refreshToken);
        validateToken(refreshToken);
        String[] tokens = tokenService.createNewToken(request);
        String newAccessToken = tokens[0];
        String newRefreshToken = tokens[1];
        response.addCookie(
            jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, newRefreshToken));

        response.setStatus(HttpServletResponse.SC_OK); // 302 Found 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            "{\"token\" : \"" + newAccessToken + "\"}"
        );
//      sendErrorResponse(response, "토큰이 만료되었습니다.");
      } catch (Exception exc) {
        sendErrorResponse(response, "잘못된 리플레시 토큰입니다.");
        return;
      }
    } catch (Exception e) {
      sendErrorResponse(response, "잘못된 토큰입니다.");
      return;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * 로그인 및 OAuth2 관련 요청인지 확인하는 메서드
   *
   * @param uri 요청된 URI
   * @return true면 필터링 제외
   */
  private boolean isLoginRequest(String uri) {
    return uri.matches(".*/login(?:/.*)?$") || uri.matches(".*/oauth2(?:/.*)?$");
  }

  private boolean isSinginRequest(String uri) {
    return uri.matches(".*/sign-in(?:/.*)?$");
  }

  /**
   * JWT 토큰을 검증하는 메서드
   *
   * @param token 검증할 JWT 토큰
   * @throws InvalidInputException 잘못된 토큰일 경우 예외 발생
   */
  private void validateToken(String token) {
    jwtUtil.isExpired(token);
    jwtUtil.isIssuer(token);
    String category = jwtUtil.getCategory(token);
    if (!TokenSettings.ACCESS_TOKEN_CATEGORY.equals(category)) {
      throw new InvalidInputException(TokenErrorCode.TOKEN_CATEGORY_MISS_MATCH);
    }
  }

  /**
   * JWT 토큰을 기반으로 사용자 인증을 수행하는 메서드
   *
   * @param token 인증할 JWT 토큰
   */
  private void authenticateUser(String token) {
    Long userId = Long.parseLong(jwtUtil.getUserId(token));
    User user = userRepository.findByIdOrElseThrow(userId);
    CustomUserDetails userDetails = new CustomUserDetails(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  /**
   * 인증 실패 시 클라이언트에게 에러 응답을 보내는 메서드
   *
   * @param response HTTP 응답 객체
   * @param message  오류 메시지
   * @throws IOException
   */
  private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    try (PrintWriter writer = response.getWriter()) {
      writer.print(message);
    }
  }
}
