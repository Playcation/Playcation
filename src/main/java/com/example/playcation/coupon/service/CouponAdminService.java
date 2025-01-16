package com.example.playcation.coupon.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponRequestDto;
import com.example.playcation.coupon.dto.CouponResponseDto;
import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.coupon.repository.CouponRepository;
import jakarta.transaction.Transactional;
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
public class CouponAdminService {

  private final CouponRepository couponRepository;

  @Transactional
  public CouponResponseDto createCoupon(CouponRequestDto requestDto) {
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

  public PagingDto<CouponResponseDto> findAllCouponsAndPaging(int page) {
    Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "id"));

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

}
