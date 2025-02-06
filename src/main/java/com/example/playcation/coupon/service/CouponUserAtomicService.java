package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponUserResponseDto;
import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.OutOfStockException;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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
            couponUser.getCoupon().getName(),
            couponUser.getCoupon().getRate(), couponUser.getCoupon().getCouponType(),
            couponUser.getIssuedDate(), couponUser.getExpiredDate(),
            couponUser.getCoupon().getEvent().getTitle())).toList();

    return new PagingDto<>(couponDtoList, couponUserPage.getTotalElements());
  }

  // 쿠폰 수량 설정
  public void setCouponCount(String couponName, Long count) {
    redisTemplate.opsForValue().set("coupon:count:" + couponName, String.valueOf(count));
  }

  @Transactional
  public void requestCoupon(Long userId, String couponName) {
    // 중복 요청 검사
    Double existingUser = redisTemplate.opsForZSet()
        .score("coupon:request:" + couponName, userId.toString());
    if (existingUser != null) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATED_REQUESTED_COUPON);
    }
    log.info("재고:{}", getRemainingCouponCount(couponName));
    // **Lua 스크립트를 사용해 원자적으로 쿠폰 감소**
    Long remainingStock = decrementIfAvailable(couponName);

    if (remainingStock < 0) {
      throw new OutOfStockException(CouponErrorCode.COUPON_OUT_OF_STOCK);
    }

    // **정상적으로 쿠폰 감소 후 대기 큐 추가**
    addQueue(userId, couponName);
  }


  public Long decrementIfAvailable(String couponName) {
    String luaScript =
        "if tonumber(redis.call('get', KEYS[1])) > 0 then " +
            "    local new_stock = redis.call('decr', KEYS[1]) " +
            "    if new_stock == 0 then redis.call('set', KEYS[2], 'true') end " +
            "    return new_stock " +
            "else " +
            "    return -1 " +
            "end";

    return redisTemplate.execute(
        new DefaultRedisScript<>(luaScript, Long.class),
        Arrays.asList("coupon:count:" + couponName, "coupon_sold_out:" + couponName)
    );
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
    System.out.println(userId);


  }


}
