package com.example.playcation.util;

import com.example.playcation.common.TokenSettings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

  private SecretKey secretKey;

  public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  // 헤더에서 유저 아이디를 가져오는 메소드 ( Long 타입 )
  public Long findUserByToken(String authorizationHeader){
    String token = authorizationHeader.replace(TokenSettings.TOKEN_TYPE, "").trim();
    return Long.parseLong(this.getUserId(token));
  }

  // 토큰에서 유저 아이디를 가져오는 메소드 ( String 타입 )
  public String getUserId(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
  }
  
  // Access 토큰 / Refresh 토큰 구분
  public String getCategory(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
  }

  // 토큰의 권한을 반환
  public String getAuth(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("auth", String.class);
  }

  // 발급자를 확인하는 메소드
  public boolean isIssuer(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getIssuer().equals(TokenSettings.TOKEN_ISSUER);
  }

  // 만료 확인하는 메소드
  public Boolean isExpired(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
  }

  // 토큰을 생성하는 메소드
  public String createJwt(String category, String userId, String role, Long expiredMs) {

    return Jwts.builder()
        .issuer(TokenSettings.TOKEN_ISSUER) // 발급자 설정
        .claim("category", category)  // 토큰 카테고리 ( access / refresh )
        .claim("userId", userId)  // 토큰에 있는 유저 Id
        .claim("role", role)      // 유저의 권한
        .issuedAt(new Date(System.currentTimeMillis())) // 토큰 생성 일자
        .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 토큰 만료시간
        .signWith(secretKey)  // secretKey를 통한 토큰 암호화
        .compact();
  }
}
