package com.example.playcation.coupon.dto;

import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.enums.CouponType;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CouponResponseDto {

  private final Long couponId;

  private final String name;

  private final Long stock;

  private final BigDecimal rate;

  private final CouponType couponType;


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
