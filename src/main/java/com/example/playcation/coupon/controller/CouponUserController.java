package com.example.playcation.coupon.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.coupon.dto.CouponUserResponseDto;
import com.example.playcation.coupon.service.CouponUserService;
import com.example.playcation.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/coupons")
public class CouponUserController {

  private final CouponUserService couponUserService;
  private final JWTUtil jwtUtil;


  @GetMapping("/{couponId}")
  public ResponseEntity<CouponUserResponseDto> findUserCoupon(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable Long couponId) {
    CouponUserResponseDto responseDto = couponUserService.findUserCoupon(
        jwtUtil.findUserByToken(authorizationHeader), couponId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<PagingDto<CouponUserResponseDto>> findAllUserCouponsAndPaging(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PagingDto<CouponUserResponseDto> coupons = couponUserService.findAllUserCouponsAndPaging(
        jwtUtil.findUserByToken(authorizationHeader), page, size);

    return new ResponseEntity<>(coupons, HttpStatus.OK);
  }

  @PostMapping("{userId}/issue/{couponId}")
  public ResponseEntity<CouponUserResponseDto> getCoupon(
      @PathVariable Long userId, @PathVariable Long couponId) {
    CouponUserResponseDto response = couponUserService.getCoupon(userId, couponId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
