package com.example.playcation.coupon.entity;

import com.example.playcation.coupon.dto.CouponRequestDto;
import com.example.playcation.enums.CouponType;
import com.example.playcation.event.entity.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
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
  private BigDecimal rate;

  @Enumerated(value = EnumType.STRING)
  private CouponType couponType;

  @Column(nullable = false, updatable = false)
  private LocalDate issuedDate;

  private Long validDays;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "`event_id`", nullable = false)
  private Event event;

  public void updateCoupon(CouponRequestDto requestDto, Event event) {
    this.name = requestDto.getName();
    this.stock = requestDto.getStock();
    this.rate = requestDto.getRate();
    this.couponType = requestDto.getCouponType();
    this.issuedDate = LocalDate.now();
    this.validDays = requestDto.getValidDays();
    this.event = event;
  }

  public void updateStock(long updatedStock) {
    this.stock = updatedStock;
  }

  // 테스트용 팩토리 메서드
  public static Coupon createForTest(Long id, String name, Long count, BigDecimal discount,
      CouponType type, LocalDate issueDate, Long validDays, Event event) {
    return new Coupon(id, name, count, discount, type, issueDate, validDays, event);
  }

}
