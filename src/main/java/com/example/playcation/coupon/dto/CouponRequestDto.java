package com.example.playcation.coupon.dto;

import com.example.playcation.enums.CouponType;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class CouponRequestDto {

  private String name;

  private Long stock;

  private BigDecimal rate;

  private CouponType couponType;


  public CouponRequestDto(String name, Long stock, BigDecimal rate,
      CouponType couponType) {
    this.name = name;
    this.stock = stock;
    this.rate = rate;
    this.couponType = couponType;
  }
}
