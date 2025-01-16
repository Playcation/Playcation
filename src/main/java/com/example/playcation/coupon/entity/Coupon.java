package com.example.playcation.coupon.entity;

import com.example.playcation.coupon.dto.CouponRequestDto;
import com.example.playcation.enums.CouponType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "`coupon`")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Min(value = 0)
  private Long stock;

  @DecimalMin(value = "0.0")
  @DecimalMax(value = "100.0")
  private BigDecimal rate;

  @Enumerated(value = EnumType.STRING)
  private CouponType couponType;

  public void updateCoupon(CouponRequestDto requestDto) {
    this.name = requestDto.getName();
    this.stock = requestDto.getStock();
    this.rate = requestDto.getRate();
    this.couponType = requestDto.getCouponType();
  }
}
