package com.example.playcation.coupon;

import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.repository.RedisCouponRepository;
import com.example.playcation.coupon.service.CouponUserLockService;
import com.example.playcation.enums.CouponType;
import com.example.playcation.event.entity.Event;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    Event event = new Event(1L, "CHRISTMAS", "크리스마스 깜짝 이벤트!");
    coupon = Coupon.createForTest(1L, "TestCoupon", 100L, BigDecimal.valueOf(10),
        CouponType.PERCENT,
        LocalDate.now(), (long) 10, event);

  }

  @Test
  @DisplayName("DistributedLock 테스트")
  void CouponLockTest() throws InterruptedException {
    int numberOfThreads = 1000;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    AtomicInteger successCount = new AtomicInteger();
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    redisCouponRepository.setCouponCount(coupon.getName(), 100L);
    for (int i = 0; i < numberOfThreads; i++) {
      long userId = i + 1;
      executorService.submit(() -> {
        try {
          // 분산락 적용 메서드 호출 (락의 key는 쿠폰의 name으로 설정)
          couponUserLockService.requestCoupon(userId, coupon.getName());
          successCount.getAndIncrement();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();
    executorService.shutdown();
    System.out.println(
        "[분산 락 테스트 결과] 성공 요청 수: " + successCount.get());
    System.out.println(
        "[Redis에 남은 쿠폰 수량] " + redisCouponRepository.getRemainingCouponCount(coupon.getName()));
  }

}