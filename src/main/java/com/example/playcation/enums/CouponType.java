package com.example.playcation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponType {
  PERCENT('%'),
  WON('₩');

  private final char value;
}
