package com.example.playcation.oauth2.handler;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.enums.Role;
import com.example.playcation.oauth2.dto.OAuth2UserDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final JWTUtil jwtUtil;

  @Value("${spring.profiles.front_url}")
  private String frontUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    // 사용자 정보 가져오기
    OAuth2UserDto userDetails = (OAuth2UserDto) authentication.getPrincipal();
    String email = userDetails.getEmail();
    User user = userRepository.findByEmailOrElseThrow(email);

    // 권한(role) 가져오기
    String role = authentication.getAuthorities()
        .stream()
        .findFirst()
        .map(GrantedAuthority::getAuthority)
        .orElse(Role.USER.name());

    // 액세스 & 리프레시 토큰 생성
    String[] tokens = jwtUtil.generateTokens(user.getId().toString(), role);
    String accessToken = tokens[0];
    String refreshToken = tokens[1];

    // refresh token 쿠키 설정
    response.addCookie(jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, refreshToken));

    // access token을 프론트로 전달
    String redirectUrl = UriComponentsBuilder.fromUriString(frontUrl + "/redirect")
        .queryParam("token", accessToken)
        .build().toUriString();

    // url을 프론트로 리디렉션
    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
  }
}
