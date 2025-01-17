package com.example.playcation.coupon.dto;

import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.enums.CouponType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CouponUserResponseDto {

  private final Long couponId;

  private final String name;

  private final Long stock;

  private final BigDecimal rate;

  private final CouponType couponType;

  private final LocalDate issuedDate;

  private final LocalDate expiredDate;

  public static CouponUserResponseDto toDto(CouponUser couponUser) {
    return new CouponUserResponseDto(
        couponUser.getCoupon().getId(),
        couponUser.getCoupon().getName(),
        couponUser.getCoupon().getStock(),
        couponUser.getCoupon().getRate(),
        couponUser.getCoupon().getCouponType(),
        couponUser.getIssuedDate(),
        couponUser.getExpireDate()
    );
  }
}
