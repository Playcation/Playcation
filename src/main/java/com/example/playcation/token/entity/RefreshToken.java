package com.example.playcation.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

//@Entity
@RedisHash("refresh_token")
@Getter
@NoArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userId;

  private String refresh;

  private String expiration;

  public RefreshToken(String userId, String refresh, String expiration) {
    this.userId = userId;
    this.refresh = refresh;
    this.expiration = expiration;
  }

}
