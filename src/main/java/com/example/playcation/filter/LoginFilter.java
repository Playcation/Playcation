package com.example.playcation.filter;

import com.example.playcation.token.entity.RefreshToken;
import com.example.playcation.token.repository.TokenRepository;
import com.example.playcation.user.entity.CustomUserDetails;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final TokenRepository tokenRepository;
  private final JWTUtil jwtUtil;

  public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, TokenRepository tokenRepository) {
    this.authenticationManager = authenticationManager;
    this.tokenRepository = tokenRepository;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    String username = obtainUsername(request);
    String password = obtainPassword(request);

    System.out.println(username);

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

    return authenticationManager.authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = customUserDetails.getUserId().toString();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String role = auth.getAuthority();

    String access = jwtUtil.createJwt("access", userId, role, 600000L);
    String refresh = jwtUtil.createJwt("refresh", userId, role, 86400000L);

    //Refresh 토큰 저장
    Date date = new Date(System.currentTimeMillis() + 86400000L);
    RefreshToken refreshEntity = new RefreshToken(userId, refresh, date.toString());
    tokenRepository.save(refreshEntity);

    response.setHeader("access", access);
    response.addCookie(createCookie("refresh", refresh));
    response.setStatus(HttpStatus.OK.value());
  }

  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24*60*60);
    //cookie.setSecure(true);
    //cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }

  private void addRefreshEntity(String userId, String refresh, Long expiredMs) {

    Date date = new Date(System.currentTimeMillis() + expiredMs);

    RefreshToken refreshEntity = new RefreshToken(userId, refresh, date.toString());

    tokenRepository.save(refreshEntity);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

    response.setStatus(401);
  }
}
