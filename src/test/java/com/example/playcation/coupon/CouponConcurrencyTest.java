package com.example.playcation.coupon;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.service.CouponUserAtomicService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class CouponConcurrencyTest {

  @Autowired
  private CouponUserAtomicService couponUserService;

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    Event event = new Event(1L, "CHRISTMAS", "크리스마스 깜짝 이벤트!");
    coupon = Coupon.createForTest(1L, "TestCoupon", 100L, BigDecimal.valueOf(10),
        CouponType.PERCENT,
        LocalDate.now(), (long) 10, event);
  }

  @Test
  @DisplayName("Atomic 테스트")
  void ConcurrencyTest() throws InterruptedException {
    int numThreads = 1000;
    AtomicInteger successCount = new AtomicInteger();
    final CountDownLatch countDownLatch = new CountDownLatch(numThreads);
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
    couponUserService.setCouponCount(coupon.getName(), 100L);

    for (int i = 0; i < numThreads; i++) {
      int number = i + 1;
      executorService.execute(() -> {
        try {
          couponUserService.requestCoupon((long) number, coupon.getName());
          successCount.getAndIncrement();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          countDownLatch.countDown();
        }
      });
    }

    countDownLatch.await();
    executorService.shutdown();

    String remainingCoupons = redisTemplate.opsForValue().get("coupon:count:TestCoupon");
    System.out.println("\n\n[Atomic 테스트 결과] 성공 요청 수: " + successCount.get());
    System.out.println("[Redis에 남은 쿠폰 수량] " + remainingCoupons);

    assertThat(Integer.parseInt(remainingCoupons)).isEqualTo(0); // 남은 쿠폰은 0이어야 함
    assertThat(successCount.get()).isEqualTo(100); // 성공 요청 수는 초기 쿠폰 개수와 같아야 함
  }

}