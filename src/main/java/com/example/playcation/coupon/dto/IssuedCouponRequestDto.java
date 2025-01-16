package com.example.playcation.coupon.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class IssuedCouponRequestDto {

  private Long couponId;

  private LocalDate issuedDate;

  private LocalDate expiredDate;

}
