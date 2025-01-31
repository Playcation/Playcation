package com.example.playcation.coupon.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponRequestDto;
import com.example.playcation.coupon.dto.CouponResponseDto;
import com.example.playcation.coupon.service.CouponAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class CouponAdminController {

  private final CouponAdminService couponAdminService;

  @PostMapping("/users/{userAmount}")
  public ResponseEntity<String> createTestUsers(@PathVariable Long userAmount) {
    couponAdminService.createTestUsers(userAmount);
    return new ResponseEntity<>("유저 생성 완료", HttpStatus.CREATED);
  }

  @PostMapping
  public ResponseEntity<CouponResponseDto> createAtomicCoupon(
      @Valid @RequestBody CouponRequestDto requestDto) {
    CouponResponseDto responseDto = couponAdminService.createAtomicCoupon(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @PostMapping("/lock")
  public ResponseEntity<CouponResponseDto> createLockCoupon(
      @Valid @RequestBody CouponRequestDto requestDto) {
    CouponResponseDto responseDto = couponAdminService.createLockCoupon(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping("/{couponId}")
  public ResponseEntity<CouponResponseDto> findCoupon(@PathVariable Long couponId) {
    CouponResponseDto responseDto = couponAdminService.findCoupon(couponId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<PagingDto<CouponResponseDto>> findAllCouponsAndPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PagingDto<CouponResponseDto> coupons = couponAdminService.findAllCouponsAndPaging(page, size);

    return new ResponseEntity<>(coupons, HttpStatus.OK);
  }

  @PatchMapping("/{couponId}")
  public ResponseEntity<CouponResponseDto> updateCoupon(
      @PathVariable Long couponId, @Valid @RequestBody CouponRequestDto requestDto) {
    CouponResponseDto responseDto = couponAdminService.updateCoupon(couponId, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @PostMapping("/publish/{couponId}")
  public ResponseEntity<String> publishAtoomicCoupon(
      @PathVariable Long couponId) {
    couponAdminService.atomicPublish(couponId);
    return new ResponseEntity<>("발급 완료", HttpStatus.OK);
  }

  @PostMapping("/lockpublish/{couponId}")
  public ResponseEntity<String> publishLockCoupon(
      @PathVariable Long couponId) {
    couponAdminService.lockPublish(couponId);
    return new ResponseEntity<>("발급 완료", HttpStatus.OK);
  }

}