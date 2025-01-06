package com.example.playcation.util;

import com.example.playcation.enums.Auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {
  private final SecretKey secretKey;

  public JwtTokenProvider(@Value("${spring.jwt.service}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
  }

  // 토큰에서 사용자 아이디를 추출
  public String getUserId(String token) {
    return getClaims(token).getSubject();
  }

  // 토큰에서 역할을 추출
  public String getRole(String token) {
    return getClaims(token).get("auth", String.class);
  }

  // 토큰이 만료되었는지 확인
  public boolean isExpired(String token) {
    try {
      return getClaims(token).getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    } catch (Exception e) {
      throw new MalformedJwtException("잘못된 JWT 토큰입니다", e);
    }
  }

  // JWT 토큰 생성
  public String createToken(Long userId, Auth auth, long expirationMs) {
    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("auth", auth)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(SignatureAlgorithm.HS256,secretKey)
        .compact();
  }

  // 토큰에서 Claims 추출
  private Claims getClaims(String token) {
    if (!StringUtils.hasText(token)) {
      throw new MalformedJwtException("토큰이 비어 있습니다.");
    }

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
