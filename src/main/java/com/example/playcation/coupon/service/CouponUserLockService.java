package com.example.playcation.coupon.service;

import com.example.playcation.coupon.repository.RedisCouponRepository;
import com.example.playcation.redis.distributedLock.DistributedLock;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponUserLockService {

  private final RedisCouponRepository redisCouponRepository;

  @Transactional
  public void requestCoupon(Long userId, String couponName) {

    redisCouponRepository.findUserFromQueue(userId, couponName);
    if (redisCouponRepository.getRemainingCouponCount(couponName) > 0) {
      addQueue(userId, couponName);
      updateCouponCount(couponName);
    }
  }

  @DistributedLock(key = "T(java.lang.String).format('COUPON:%s', #couponName)")
  public void addQueue(Long userId, String couponName) {
    redisCouponRepository.addUser(userId, couponName);
  }

  @DistributedLock(key = "T(java.lang.String).format('COUPON:%s', #couponName)")
  public void updateCouponCount(String couponName) {
    redisCouponRepository.decrementCouponCount(couponName);
  }
}
