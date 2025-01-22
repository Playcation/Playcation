package com.example.playcation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Social {
  NORMAL("NORMAL"),
  NAVER("NAVER"),
  GOOGLE("GOOGLE"),
  DEFAULT_PASSWORD("0000");

  private String password;
}
