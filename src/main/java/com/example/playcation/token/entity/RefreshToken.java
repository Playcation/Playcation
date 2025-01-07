package com.example.playcation.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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
