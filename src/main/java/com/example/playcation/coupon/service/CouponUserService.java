package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponResponseDto;
import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponUserService {

  private final CouponUserRepository couponUserRepository;
  private final UserRepository userRepository;

  public CouponResponseDto findUserCoupon(Long userId, Long couponId) {
    userRepository.findByIdOrElseThrow(userId);
    CouponUser couponUser = couponUserRepository.findByUserIdAndCouponId(userId, couponId)
        .orElseThrow(() -> new NotFoundException(CouponErrorCode.COUPON_NOT_FOUND));

    return CouponResponseDto.toDto(couponUser.getCoupon());
  }

  public PagingDto<CouponResponseDto> findAllUserCouponsAndPaging(int page, Long userId) {
    Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "id"));
    userRepository.findByIdOrElseThrow(userId);
    Page<CouponUser> couponUserPage = couponUserRepository.findAll(pageable);

    List<CouponResponseDto> couponDtoList = couponUserPage.getContent().stream()
        .map(couponUser -> new CouponResponseDto(couponUser.getId(),
            couponUser.getCoupon().getName(),
            couponUser.getCoupon().getStock(),
            couponUser.getCoupon().getRate(),
            couponUser.getCoupon().getCouponType()))
        .toList();

    return new PagingDto<>(couponDtoList, couponUserPage.getTotalElements());
  }

}
