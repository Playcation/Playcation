package com.example.playcation.coupon.repository;

import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.NotFoundException;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

  boolean existsByNameAndRate(final String name, BigDecimal rate);

  default Coupon findByIdOrElseThrow(Long id) {
    Coupon coupon = findById(id).orElseThrow(
        () -> new NotFoundException(CouponErrorCode.COUPON_NOT_FOUND));

    return coupon;
  }

  // eventId가 특정 값과 일치하는 쿠폰들만 조회
  Page<Coupon> findByEventId(long eventId, Pageable pageable);
}
