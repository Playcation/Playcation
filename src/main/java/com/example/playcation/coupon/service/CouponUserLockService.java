package com.example.playcation.coupon.service;

import com.example.playcation.coupon.repository.RedisCouponRepository;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.OutOfStockException;
import com.example.playcation.redis.distributedLock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponUserLockService {

  private final RedisCouponRepository redisCouponRepository;

  @DistributedLock(key = "T(java.lang.String).format('coupon:count:%s', #couponName)")
  public void requestCoupon(Long userId, String couponName) {
    log.info("재고:{}", redisCouponRepository.getRemainingCouponCount(couponName));
    if (redisCouponRepository.getRemainingCouponCount(couponName) <= 0) {
      throw new OutOfStockException(CouponErrorCode.COUPON_OUT_OF_STOCK);
    }
    redisCouponRepository.findUserFromQueue(userId, couponName);
    addQueue(userId, couponName);
    redisCouponRepository.decrementAndGetCouponCount(couponName);

  }

  @DistributedLock(key = "T(java.lang.String).format('COUPON:%s', #couponName)")
  public void addQueue(Long userId, String couponName) {
    redisCouponRepository.addUser(userId, couponName);
  }


}
