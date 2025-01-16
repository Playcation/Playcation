package com.example.playcation.coupon.dto;

import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.enums.CouponType;
import java.math.BigDecimal;

public class CouponResponseDto {

  private Long couponId;

  private String name;

  private Long stock;

  private BigDecimal rate;

  private CouponType couponType;

  public CouponResponseDto(Long couponId, String name, Long stock, BigDecimal rate,
      CouponType couponType) {
    this.couponId = couponId;
    this.name = name;
    this.stock = stock;
    this.rate = rate;
    this.couponType = couponType;
  }

  public static CouponResponseDto toDto(Coupon coupon) {
    return new CouponResponseDto(
        coupon.getId(),
        coupon.getName(),
        coupon.getStock(),
        coupon.getRate(),
        coupon.getCouponType()
    );
  }
}
