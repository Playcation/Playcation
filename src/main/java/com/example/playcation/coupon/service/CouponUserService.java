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

  @Transactional
  public CouponUserResponseDto getCoupon(Long userId, Long couponId) {
    // 쿠폰 조회 및 재고 확인
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);
    User user = userRepository.findByIdOrElseThrow(userId);

    if (coupon.getStock() <= 0) {
      throw new InvalidInputException(CouponErrorCode.COUPON_OUT_OF_STOCK);
    }

    // 쿠폰 재고 감소
    coupon.updateStock();

    // 쿠폰 발급
    CouponUser couponUser = CouponUser.builder()
        .user(user)
        .coupon(coupon)
        .issuedDate(coupon.getIssuedDate())
        .expiredDate(coupon.getIssuedDate().plusDays(coupon.getValidDays()))
        .build();

    couponUserRepository.save(couponUser);

    return CouponUserResponseDto.toDto(couponUser);
  }
}
