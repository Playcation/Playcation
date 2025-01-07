package com.example.playcation.token.controller;

import com.example.playcation.token.service.TokenService;
import com.example.playcation.user.service.UserService;
import com.example.playcation.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class TokenController {

  private final TokenService tokenService;

  @PostMapping("/refresh")
  public ResponseEntity<String> refresh(
      HttpServletRequest request,
      HttpServletResponse response
  ){

    String[] tokens = tokenService.createNewToken(request);

    //response
    response.setHeader("access", tokens[0]);
    response.addCookie(createCookie("refresh", tokens[1]));

    return new ResponseEntity<>("토큰이 발급되었습니다. : " + tokens[0], HttpStatus.OK);
  }

  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24*60*60);
    //cookie.setSecure(true);
    //cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }
}
