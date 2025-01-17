package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponRequestDto;
import com.example.playcation.coupon.dto.CouponResponseDto;
import com.example.playcation.coupon.dto.IssuedCouponRequestDto;
import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponRepository;
import com.example.playcation.coupon.repository.CouponUserRepository;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponAdminService {

  private final UserRepository userRepository;
  private final CouponRepository couponRepository;
  private final CouponUserRepository couponUserRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Transactional
  public void createTestUsers(Long userAmount) {
    for (int i = 1; i <= userAmount; i++) {
      userRepository.save(
          new User("email" + i + "@example.com", bCryptPasswordEncoder.encode("q1w2e3r4!!"),
              "name" + i, Role.USER,
              Social.NORMAL));
    }
  }

  @Transactional
  public CouponResponseDto createCoupon(CouponRequestDto requestDto) {
    boolean couponExists = couponRepository.existsByNameAndRate(
        requestDto.getName(),
        requestDto.getRate()
    );

    if (couponExists) {
      throw new DuplicatedException(CouponErrorCode.DUPLICATE_COUPON);
    }
    Coupon coupon = Coupon.builder()
        .name(requestDto.getName())
        .stock(requestDto.getStock())
        .rate(requestDto.getRate())
        .couponType(requestDto.getCouponType())
        .build();

    couponRepository.save(coupon);

    return CouponResponseDto.toDto(coupon);
  }

  public CouponResponseDto findCoupon(Long couponId) {
    Coupon coupon = couponRepository.findByIdOrElseThrow(couponId);

    return CouponResponseDto.toDto(coupon);
  }

  public PagingDto<CouponResponseDto> findAllCouponsAndPaging(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));

    Page<Coupon> couponPage = couponRepository.findAll(pageable);

    List<CouponResponseDto> couponDtoList = couponPage.getContent().stream()
        .map(coupon -> new CouponResponseDto(coupon.getId(), coupon.getName(), coupon.getStock(),
            coupon.getRate(), coupon.getCouponType()))
        .toList();

    return new PagingDto<>(couponDtoList, couponPage.getTotalElements());
  }

  @Transactional
  public CouponResponseDto updateCoupon(Long couponId, CouponRequestDto requestDto) {
    Coupon newCoupon = couponRepository.findByIdOrElseThrow(couponId);

    newCoupon.updateCoupon(requestDto);

    couponRepository.save(newCoupon);

    return CouponResponseDto.toDto(newCoupon);
  }

  @Transactional
  public void issueCoupon(Long userId, IssuedCouponRequestDto requestDto) {
    // 사용자와 쿠폰 조회
    User user = userRepository.findByIdOrElseThrow(userId);
    Coupon coupon = couponRepository.findByIdOrElseThrow(requestDto.getCouponId());

    // 사용자가 USER인지 확인
    Role userRole = user.getRole();
    if (userRole.equals(Role.MANAGER) || (userRole.equals(Role.ADMIN))) {
      throw new NoAuthorizedException(CouponErrorCode.NO_AUTHORIZED_COUPON);
    }

    // 쿠폰 재고 확인
    if (coupon.getStock() <= 0) {
      throw new InvalidInputException(CouponErrorCode.COUPON_OUT_OF_STOCK);
    }

    // 쿠폰 발급 정보 저장
    CouponUser couponUser = CouponUser.builder()
        .user(user)
        .coupon(coupon)
        .issuedDate(requestDto.getIssuedDate())
        .expireDate(requestDto.getExpiredDate()) // 만료일을 기본 30일로 설정
        .build();

    couponUserRepository.save(couponUser);

    // 쿠폰 재고 감소
    coupon.updateStock();
  }
}
