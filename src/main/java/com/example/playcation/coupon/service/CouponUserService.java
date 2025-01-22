package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponUserResponseDto;
import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
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

  //  @Transactional
//  public CouponUserResponseDto getCoupon(Long userId, Long couponId) {
//    // 쿠폰 조회 및 재고 확인
//    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);
//    User user = userRepository.findByIdOrElseThrow(userId);
//
//    if (coupon.getStock() <= 0) {
//      throw new InvalidInputException(CouponErrorCode.COUPON_OUT_OF_STOCK);
//    }
//
//    // 쿠폰 재고 감소
//    coupon.updateStock();
//
//    // 쿠폰 발급
//    CouponUser couponUser = CouponUser.builder()
//        .user(user)
//        .coupon(coupon)
//        .issuedDate(coupon.getIssuedDate())
//        .expiredDate(coupon.getIssuedDate().plusDays(coupon.getValidDays()))
//        .build();
//
//    couponUserRepository.save(couponUser);
//
//    return CouponUserResponseDto.toDto(couponUser);
//  }
// 남은 이벤트 쿠폰 수량 가져오기
  public int getRemainingCouponCount(Coupon coupon) {
    String countStr = redisTemplate.opsForValue().get(coupon.getId() + "_COUNT");
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
    Long size = redisTemplate.opsForZSet().size(coupon.getName());
    return size != null ? size : 0L; // null일 경우 0 반환
  }

  // 큐에 사용자 추가
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
    redisTemplate.opsForZSet().add(coupon.getName(), user.getEmail(), now);
    log.info("큐에 추가됨 - {} at {}ms", user.getName(), now);

    // 큐에 추가된 후 바로 큐를 처리
    processQueue(couponId);

    return true;
  }

  // 큐를 처리하는 메서드
  public void processQueue(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);
    // 쿠폰이 남아 있는지 확인한 후 발행 시도
    if (getRemainingCouponCount(coupon) > 0) {
      log.info("이벤트 처리 중: {}", coupon);
      couponAdminService.publish(coupon);
    } else {
      log.info("===== 선착순 이벤트가 종료되었습니다: {} =====", coupon);
    }
  }

  // 해당 이벤트의 쿠폰을 이미 받았는지 확인
  public boolean canIssueCoupon(Long userId, Long couponId) {
    return couponUserRepository.findByUserIdAndCouponId(userId, couponId).isEmpty();
  }

}
