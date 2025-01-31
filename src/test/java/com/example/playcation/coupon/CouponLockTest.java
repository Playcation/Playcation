package com.example.playcation.coupon;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.repository.RedisCouponRepository;
import com.example.playcation.coupon.service.CouponUserLockService;
import com.example.playcation.enums.CouponType;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class CouponLockTest {

  @Autowired
  private CouponUserLockService couponUserLockService;

  @Autowired
  private RedisCouponRepository redisCouponRepository;

  @Autowired
  private CouponRepository couponRepository;


  private Coupon coupon;

  @Autowired
  private RedissonClient redissonClient;

  @BeforeEach
  void setUp() {
    coupon = Coupon.createForTest(1L, "TestCoupon", 100L, BigDecimal.valueOf(10),
        CouponType.PERCENT,
        LocalDate.now(), (long) 10);

  }

  @Test
  void CouponLockTest() throws InterruptedException {
    int numberOfThreads = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    redisCouponRepository.setCouponCount(coupon.getName(), (long) numberOfThreads);
    for (int i = 0; i < numberOfThreads; i++) {
      long number = i + 1;
      executorService.submit(() -> {
        try {
          // 분산락 적용 메서드 호출 (락의 key는 쿠폰의 name으로 설정)
          couponUserLockService.requestCoupon(number, coupon.getName());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    assertThat(redisCouponRepository.getRemainingCouponCount(coupon.getName())).isZero();
    System.out.println(
        "잔여 쿠폰 개수 = " + redisCouponRepository.getRemainingCouponCount(coupon.getName()));
  }

}