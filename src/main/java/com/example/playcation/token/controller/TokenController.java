package com.example.playcation.token.controller;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.token.service.TokenService;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TokenController {

  private final TokenService tokenService;
  private final JWTUtil jwtUtil;

  // 토큰이 만료되었을 때 refresh 토큰을 확인한 후 토큰을 재발급해주는 api
  @PostMapping("/refresh")
  public ResponseEntity<String> refresh(
      HttpServletRequest request,
      HttpServletResponse response
  ){

    String[] tokens = tokenService.createNewToken(request);

    //response
    response.setHeader(TokenSettings.ACCESS_TOKEN_CATEGORY, tokens[0]);
    response.addCookie(jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, tokens[1]));

    return new ResponseEntity<>("토큰이 발급되었습니다. : " + tokens[0], HttpStatus.OK);
  }
}
