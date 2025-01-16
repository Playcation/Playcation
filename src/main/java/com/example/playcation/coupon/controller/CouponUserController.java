package com.example.playcation.coupon.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.coupon.dto.CouponResponseDto;
import com.example.playcation.coupon.service.CouponUserService;
import com.example.playcation.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<CouponResponseDto> findUserCoupon(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable Long couponId) {
    CouponResponseDto responseDto = couponUserService.findUserCoupon(
        jwtUtil.findUserByToken(authorizationHeader), couponId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<PagingDto<CouponResponseDto>> findAllUserCouponsAndPaging(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestParam(defaultValue = "0") int page) {
    PagingDto<CouponResponseDto> coupons = couponUserService.findAllUserCouponsAndPaging(page,
        jwtUtil.findUserByToken(authorizationHeader));

    return new ResponseEntity<>(coupons, HttpStatus.OK);
  }

}
