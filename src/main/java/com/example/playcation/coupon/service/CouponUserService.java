package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponUserResponseDto;
import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponUserService {

  private final CouponUserRepository couponUserRepository;
  private final UserRepository userRepository;
  private final CouponRepository couponRepository;
  private final RedisTemplate<String, String> redisTemplate;


  public CouponUserResponseDto findUserCoupon(Long userId, Long couponId) {
    userRepository.findByIdOrElseThrow(userId);
    CouponUser couponUser = couponUserRepository.findByUserIdAndCouponId(userId, couponId)
        .orElseThrow(() -> new NotFoundException(CouponErrorCode.COUPON_NOT_FOUND));

    return CouponUserResponseDto.toDto(couponUser);
  }

  public PagingDto<CouponUserResponseDto> findAllUserCouponsAndPaging(Long userId, int page,
      int size) {
    userRepository.findByIdOrElseThrow(userId);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "couponId"));
    Page<CouponUser> couponUserPage = couponUserRepository.findAllByUserId(userId, pageable);

    List<CouponUserResponseDto> couponDtoList = couponUserPage.getContent().stream().map(
        couponUser -> new CouponUserResponseDto(couponUser.getCoupon().getId(),
            couponUser.getCoupon().getName(), couponUser.getCoupon().getStock(),
            couponUser.getCoupon().getRate(), couponUser.getCoupon().getCouponType(),
            couponUser.getIssuedDate(), couponUser.getExpiredDate())).toList();

    return new PagingDto<>(couponDtoList, couponUserPage.getTotalElements());
  }

  // 남은 이벤트 쿠폰 수량 가져오기
  public int getRemainingCouponCount(Coupon coupon) {
    String countStr = redisTemplate.opsForValue().get("coupon:count:" + coupon.getId());
    if (countStr != null) {
      try {
        return Integer.parseInt(countStr);
      } catch (NumberFormatException e) {
        log.error("Redis에서 카운트 값을 파싱하지 못했습니다", e);
      }
    }
    return 0;
  }

  // 큐에 남아있는 사용자 수 가져오기
  public long getSize(long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);
    Long size = redisTemplate.opsForZSet().size("coupon:request:" + coupon.getName());
    return size != null ? size : 0L; // null일 경우 0 반환
  }

  // 큐에 사용자 추가
  @Transactional
  public boolean addQueue(Long couponId, Long userId) {
    User user = userRepository.findByIdOrElseThrow(userId);
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    // 사용자가 이미 해당 이벤트의 쿠폰을 받았는지 확인
    if (!canIssueCoupon(userId, couponId)) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATE_COUPON);
    }

    // 잘 돌아갈 시 리팩토링 요망
    if (redisTemplate == null) {
      throw new IllegalStateException("RedisTemplate은 null일 수 없습니다");
    }

    if (getRemainingCouponCount(coupon) <= 0) {
      log.error("쿠폰이 더 이상 남아 있지 않습니다: {}", coupon);
      throw new InvalidInputException(CouponErrorCode.COUPON_OUT_OF_STOCK);
    }

    final long now = System.currentTimeMillis();
    redisTemplate.opsForZSet().add("coupon:request:" + coupon.getName(), user.getEmail(), now);
    log.info("큐에 추가됨 - {} at {}ms", user.getName(), now);

    // 큐에 추가된 후 바로 큐를 처리
    processQueue(couponId);

    return true;
  }

  // 큐를 처리하는 메서드
  @Transactional
  public void processQueue(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);
    // 쿠폰이 남아 있는지 확인한 후 발행 시도
    if (getRemainingCouponCount(coupon) > 0) {
      log.info("이벤트 처리 중: {}", coupon);
      publish(coupon);
    } else {
      log.info("===== 선착순 이벤트가 종료되었습니다: {} =====", coupon);
    }
  }

  // 해당 이벤트의 쿠폰을 이미 받았는지 확인
  public boolean canIssueCoupon(Long userId, Long couponId) {
    return couponUserRepository.findByUserIdAndCouponId(userId, couponId).isEmpty();
  }

  // 큐에 있는 사용자들에게 쿠폰 발행
  @Transactional
  public void publish(Coupon coupon) {
    List<User> users = getUsersFromQueue(coupon);
    for (User user : users) {
      issueCoupon(coupon, user);
      decrementCouponCount(coupon);
      redisTemplate.opsForZSet().remove("coupon:request:" + coupon.getName(), user.getEmail());
    }
  }

  // 이벤트 쿠폰 수량 설정
  public void setCouponCount(Long couponId, long count) {
    redisTemplate.opsForValue().set("coupon:count:" + couponId, String.valueOf(count));
  }

  // 이벤트 쿠폰 수량 감소
  public void decrementCouponCount(Coupon coupon) {
    redisTemplate.opsForValue().decrement("coupon:count:" + coupon.getId());
  }

  // 큐에 있는 사용자 목록 가져오기
  private List<User> getUsersFromQueue(Coupon coupon) {
    Set<String> emails = redisTemplate.opsForZSet()
        .range("coupon:request:" + coupon.getName(), 0, -1);
    List<User> users = new ArrayList<>();
    if (emails != null) {
      for (String email : emails) {
        userRepository.findByEmail(email).ifPresent(users::add);
      }
    }
    return users;
  }

  // 사용자에게 쿠폰 발행
  @Transactional
  public void issueCoupon(Coupon coupon, User user) {
    // 쿠폰 발급
    CouponUser couponUser = CouponUser.builder()
        .user(user)
        .coupon(coupon)
        .issuedDate(coupon.getIssuedDate())
        .expiredDate(coupon.getIssuedDate().plusDays(coupon.getValidDays()))
        .build();

    couponUserRepository.save(couponUser);
    log.info("'{}'에게 {} 쿠폰이 발급되었습니다", user.getEmail(), coupon.getName());
  }
}
