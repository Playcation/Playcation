package com.example.playcation.filter;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.token.entity.RefreshToken;
import com.example.playcation.token.repository.TokenRepository;
import com.example.playcation.user.entity.CustomUserDetails;
import com.example.playcation.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final TokenRepository tokenRepository;
  private final JWTUtil jwtUtil;

  public CustomLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, TokenRepository tokenRepository) {
    this.authenticationManager = authenticationManager;
    this.tokenRepository = tokenRepository;
    this.jwtUtil = jwtUtil;
  }

  // 로그인을 진행하는 필터 ( application/json 형식으로 데이터를 받아온다. )
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, String> loginData = objectMapper.readValue(request.getInputStream(), Map.class);  // json 형식의 데이터를 Map 으로 변환

      String email = loginData.get("email");
      String password = loginData.get("password");

      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null); // UserDetails 를 통해 로그인 확인

      return authenticationManager.authenticate(authToken);
    }catch(IOException e){
      throw new RuntimeException(e);
    }
  }

  // 로그인이 성공 했을 때 토큰을 발급해주는 로직
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = customUserDetails.getUserId().toString();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();

    String role = auth.getAuthority();

    String access = TokenSettings.TOKEN_TYPE +  jwtUtil.createJwt(TokenSettings.ACCESS_TOKEN_CATEGORY, userId,
        role, TokenSettings.ACCESS_TOKEN_EXPIRATION);
    String refresh = jwtUtil.createJwt(TokenSettings.REFRESH_TOKEN_CATEGORY, userId, role, TokenSettings.REFRESH_TOKEN_EXPIRATION);

    //Refresh 토큰 저장
    Date date = new Date(System.currentTimeMillis() + TokenSettings.REFRESH_TOKEN_EXPIRATION);
    RefreshToken refreshEntity = new RefreshToken(userId, refresh, date.toString());
    tokenRepository.save(refreshEntity);

    response.setHeader(TokenSettings.ACCESS_TOKEN_CATEGORY, access);
    response.addCookie(createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, refresh));
    response.setStatus(HttpStatus.OK.value());
  }

  // 쿠키에 리플레시 토큰을 담기위해 쿠키를 생성하는 로직
  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    // 쿠키 1일 유지
    cookie.setMaxAge(TokenSettings.COOKIE_EXPIRATION);
    //cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }

  // 로그인이 실패 했을 때 로직 ( 토큰 / 쿠키를 생성하지 않는다. )
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

    response.setStatus(401);
  }
}
