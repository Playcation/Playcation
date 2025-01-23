package com.example.playcation.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.playcation.PlaycationApplication;
import com.example.playcation.config.RedisTestContainerConfig;
import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.service.CouponUserService;
import com.example.playcation.enums.CouponType;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest(classes = {PlaycationApplication.class, RedisTestContainerConfig.class})
@Transactional
class CouponServiceTest {

  @Autowired
  private CouponUserService couponUserService;

  @MockitoBean
  private CouponRepository couponRepository;

  @MockitoBean
  private UserRepository userRepository;

  @Autowired
  @MockitoBean
  private RedisTemplate<String, String> redisTemplate;

  @Test
  @DisplayName("Redis의 싱글 스레드 특성을 이용한 쿠폰 동시 발급 테스트")
  void concurrencyTestWithRedis() {
    // Redis 초기화
    String couponKey = "coupon:count:1"; // 쿠폰 ID 1의 Redis 키
    redisTemplate.opsForValue().set(couponKey, "100"); // 쿠폰 초기 개수 100 설정

    // Mocking
    Mockito.when(couponRepository.findByIdOrElseThrow(1L))
        .thenReturn(
            new Coupon(1L, "TestCoupon", (long) 100, BigDecimal.valueOf(10), CouponType.PERCENT,
                LocalDate.now(), (long) 10));
    Mockito.when(userRepository.findByIdOrElseThrow(Mockito.anyLong()))
        .thenAnswer(invocation -> {
          Long userId = invocation.getArgument(0);
          System.out.println("habin : " + userId);
          return new User(
              "user" + userId + "@example.com", // email
              "qwer1234!!",                   // password
              "User",           // name
              "username",             // username
              Role.USER,                       // role (example value)
              Social.NORMAL                      // social (example value)
          );
        });
    // 병렬 요청 실행
    AtomicInteger successCounter = new AtomicInteger();
    IntStream.range(0, 100).parallel().forEach(i -> {
      try {
        boolean result = couponUserService.addQueue(1L, (long) i);
        if (result) {
          successCounter.incrementAndGet();
        }
      } catch (Exception e) {
        // 무시: 실패한 요청은 성공 카운트에 포함되지 않음
      }
    });

    // 결과 확인
    String remainingCoupons = redisTemplate.opsForValue().get("coupon:count:1");
    System.out.println("\n\n[테스트 결과] 성공 요청 수: " + successCounter.get());
    System.out.println("[Redis에 남은 쿠폰 수량] " + remainingCoupons);

    assertThat(Integer.parseInt(remainingCoupons)).isEqualTo(0); // 남은 쿠폰은 0이어야 함
    assertThat(successCounter.get()).isEqualTo(100); // 성공 요청 수는 초기 쿠폰 개수와 같아야 함
  }
}
