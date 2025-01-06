package com.example.playcation.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class TokenUtil {

  private final JwtTokenProvider jwtTokenProvider;

  public Long findUserByToken(String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "").trim();
    return Long.parseLong(jwtTokenProvider.getUserId(token));
  }
}
