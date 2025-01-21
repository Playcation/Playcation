package com.example.playcation.enums;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Grade {
  VVIP( new BigDecimal(100)),
  VIP( new BigDecimal(50)),
  NORMAL( new BigDecimal(10));

  private BigDecimal freePoint;
}
