package com.example.playcation.enums;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Grade {
  VVIP( new BigDecimal(100), new BigDecimal(50_000)),
  VIP( new BigDecimal(50), new BigDecimal(20_000)),
  NORMAL( new BigDecimal(10), BigDecimal.ZERO);

  private BigDecimal freePoint;
  private BigDecimal gradeCriteria;
}
