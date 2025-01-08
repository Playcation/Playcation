package com.example.playcation.oauth2.handler;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.oauth2.dto.OAuth2UserDto;
import com.example.playcation.token.entity.RefreshToken;
import com.example.playcation.token.repository.TokenRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final JWTUtil jwtUtil;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    //OAuth2User
    OAuth2UserDto userDetails = (OAuth2UserDto) authentication.getPrincipal();

    String email = userDetails.getEmail();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    User user = userRepository.findByEmailOrElseThrow(email);

    String access = TokenSettings.TOKEN_TYPE + jwtUtil.createJwt(TokenSettings.ACCESS_TOKEN_CATEGORY, user.getId().toString(), role, TokenSettings.ACCESS_TOKEN_EXPIRATION);
    String refresh = jwtUtil.createJwt(TokenSettings.REFRESH_TOKEN_CATEGORY, user.getId().toString(), role, TokenSettings.REFRESH_TOKEN_EXPIRATION);

    Date date = new Date(System.currentTimeMillis() + TokenSettings.REFRESH_TOKEN_EXPIRATION);
    RefreshToken refreshToken = new RefreshToken(user.getId().toString(), refresh, date.toString());
    tokenRepository.save(refreshToken);

    response.setHeader(TokenSettings.ACCESS_TOKEN_CATEGORY, access);
    response.addCookie(jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, refresh));
    response.sendRedirect("http://localhost:3000/");
  }
}
