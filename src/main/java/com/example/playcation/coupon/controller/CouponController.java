package com.example.playcation.coupon.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.coupon.dto.CouponResponseDto;
import com.example.playcation.coupon.service.CouponAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

  private final CouponAdminService couponAdminService;

  @GetMapping("/{eventId}")
  public ResponseEntity<PagingDto<CouponResponseDto>> findAllCouponsAndPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size, @PathVariable long eventId) {
    PagingDto<CouponResponseDto> coupons = couponAdminService.findAllCouponsAndPaging(eventId, page,
        size);

    return new ResponseEntity<>(coupons, HttpStatus.OK);
  }
}
