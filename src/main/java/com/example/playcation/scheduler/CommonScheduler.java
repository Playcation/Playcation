package com.example.playcation.scheduler;

import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.coupon.service.CouponUserService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class CommonScheduler {

  private final CouponUserService couponUserService;

  /**
   * 유저에게 발급된 쿠폰들 중 유효 기간이 지난 것들을 삭제
   */
  @Scheduled(cron = "0 * * * * *")
  public void runDeleteExpireCoupon() {

    couponUserService.deleteExpiredCoupons();
  }
}
