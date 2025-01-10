package com.example.playcation.token.entity;

import com.example.playcation.common.TokenSettings;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

//@Entity
@Getter
@NoArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = TokenSettings.REFRESH_TOKEN_EXPIRATION)
public class RefreshToken {

  @Id
  private String id;

  private String userId;

  private String refresh;

  private String expiration;

  public RefreshToken(String userId, String refresh, String expiration) {
    this.userId = userId;
    this.refresh = refresh;
    this.expiration = expiration;
  }
}
