package com.example.playcation.coupon;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.coupon.service.CouponAdminService;
import com.example.playcation.coupon.service.CouponUserService;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class CouponServiceTest {

  @Autowired
  private CouponUserService couponUserService;

  @Autowired
  private CouponAdminService couponAdminService;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private MockMvc mockMvc;
  //
  @MockBean
  private UserRepository userRepository;
  //
  @MockBean
  private CouponUserRepository couponUserRepository;

  @Mock
  private JWTUtil jwtUtil;

  private String authorizationHeader;

  @BeforeEach
  void setUp() {
    redisTemplate.getConnectionFactory().getConnection().flushAll(); // 테스트 전에 Redis 초기화
  }

  @Test
  void requestCouponTest() throws Exception {
    final long couponId = 1L;
    final int numberOfUsers = 10;
    final int couponLimit = 3;

    // Redis에 이벤트 쿠폰 수량 설정
    couponAdminService.setCouponCount(couponId, couponLimit);

    // 가짜 사용자 생성 메소드 호출
    List<User> users = createMockUsers(numberOfUsers);

    // JWTUtil Mock 설정
    when(jwtUtil.findUserByToken(any(String.class))).thenAnswer(invocation -> {
      String token = invocation.getArgument(0);
      if (token.startsWith("Bearer mock-jwt-token-for-user-")) {
        return Long.parseLong(token.replace("Bearer mock-jwt-token-for-user-", ""));
      }
      return null; // 잘못된 토큰 처리
    });

    // 모든 요청이 처리될 때까지 기다리기 위해 CountDownLatch 사용
    CountDownLatch enqueueLatch = new CountDownLatch(numberOfUsers);
    AtomicInteger failureCount = new AtomicInteger();
    for (User user : users) {
      // Mock JWT Token 생성
      String mockToken = "Bearer mock-jwt-token-for-user-" + user.getId();
      // UserRepository Mock 설정
      when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      when(jwtUtil.findUserByToken("Authorization")).thenReturn(user.getId());
      // MockMvc 요청 수행
      mockMvc.perform(post("/coupons/request/" + couponId)
              .with(csrf())
              .header("Authorization", mockToken) // Authorization 헤더 설정
              .contentType(MediaType.APPLICATION_JSON))
          .andDo(result -> {
            System.out.println("Response Status: " + result.getResponse().getStatus());
            System.out.println("User ID: " + user.getId());
            if (result.getResponse().getContentAsString().contains("쿠폰 요청 추가에 실패하였습니다.")) {
              assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
              failureCount.incrementAndGet();
            } else {
              assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
            }
            enqueueLatch.countDown();
          });
    }

    assertTrue(enqueueLatch.await(10, TimeUnit.SECONDS), "타임아웃 내에 모든 사용자가 큐에 추가되지 못했습니다.");

    // 스케줄러가 큐를 처리할 시간을 주기 위해 대기
    Thread.sleep(5000);

    // 쿠폰을 받은 사용자 수 검증
    verify(couponUserRepository, times(couponLimit)).save(any(CouponUser.class));

    // 실패한 요청 수 검증
    assertEquals(numberOfUsers - couponLimit, failureCount.get(), "실패한 요청 수가 예상과 다릅니다.");

    // 큐에 남아있는 사용자 수 검증
    long remainingUsersInQueue = couponUserService.getSize(couponId);
    assertEquals(0, remainingUsersInQueue, "큐에 사용자가 남아 있지 않아야 합니다.");
  }

  // 가짜 사용자 생성
  private List<User> createMockUsers(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> User.builder()
            .id((long) i + 1)
            .email("unique" + System.nanoTime() + "@example.com")
            .password("qwer1234!!")
            .name("User" + System.nanoTime())
            .role(Role.USER)
            .social(Social.NORMAL)
            .build())
        .collect(Collectors.toList());
  }
}