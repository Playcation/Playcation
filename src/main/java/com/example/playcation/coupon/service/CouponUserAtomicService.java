package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponUserResponseDto;
import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
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
public class CouponUserAtomicService {

  private final CouponUserRepository couponUserRepository;
  private final UserRepository userRepository;
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

  // 쿠폰 수량 설정
  public void setCouponCount(String couponName, Long count) {
    redisTemplate.opsForValue().set("coupon:count:" + couponName, String.valueOf(count));
  }

  @Transactional
  public void requestCoupon(Long userId, String couponName) {
    Double existingUser = redisTemplate.opsForZSet()
        .score("coupon:request:" + couponName, userId.toString());
    if (existingUser != null) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATED_REQUESTED_COUPON);
    }
    if (getRemainingCouponCount(couponName) > 0) {
      addQueue(userId, couponName);
      decrementCouponCount(couponName);
    }
  }

  // 남은 쿠폰 수량 가져오기
  public int getRemainingCouponCount(String couponName) {
    String countStr = redisTemplate.opsForValue().get("coupon:count:" + couponName);
    if (countStr != null) {
      return Integer.parseInt(countStr);
    }
    return 0;
  }

  // 쿠폰 수량 감소
  @Transactional
  public void decrementCouponCount(String couponName) {
    redisTemplate.opsForValue().decrement("coupon:count:" + couponName);
  }

  @Transactional
  public void addQueue(Long userId, String couponName) {
    long now = System.currentTimeMillis();
    redisTemplate.opsForZSet().add("coupon:request:" + couponName, String.valueOf(userId), now);
  }


}
