package com.example.playcation.coupon.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.coupon.dto.CouponUserResponseDto;
import com.example.playcation.coupon.service.CouponUserAtomicService;
import com.example.playcation.coupon.service.CouponUserLockService;
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

  private final CouponUserAtomicService couponUserService;
  private final CouponUserLockService couponUserLockService;
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

  @PostMapping("/request/{couponName}")
  public ResponseEntity<String> requestAtomicCoupon(@PathVariable("couponName") String couponName,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    boolean existCoupon = couponUserService.existCoupon(couponName);
    // 쿠폰 발급 큐에 사용자 추가 시도
    couponUserService.requestCoupon(jwtUtil.findUserByToken(authorizationHeader), couponName);

    if (!existCoupon) {
      return new ResponseEntity<>("쿠폰 요청 실패.", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>("쿠폰 요청 완료.", HttpStatus.OK);
  }

  // 분산락
  @PostMapping("/lockrequest/{couponName}")
  public ResponseEntity<String> requestLockCoupon(
      @PathVariable("couponName") String couponName,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    boolean existCoupon = couponUserService.existCoupon(couponName);
    // 쿠폰 발급 큐에 사용자 추가 시도
    couponUserLockService.requestCoupon(jwtUtil.findUserByToken(authorizationHeader), couponName);
    if (!existCoupon) {
      return new ResponseEntity<>("쿠폰 요청 실패.", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>("쿠폰 요청 완료.", HttpStatus.OK);
  }
}
