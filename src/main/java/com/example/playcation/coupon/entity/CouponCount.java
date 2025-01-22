package com.example.playcation.coupon.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponCount {

  private Long couponId;
  private int remainingCount;

  public CouponCount(Long couponId, int count) {
    this.couponId = couponId;
    this.remainingCount = count;
  }
}