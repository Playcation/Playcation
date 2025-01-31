package com.example.playcation.coupon.repository;

import com.example.playcation.coupon.entity.Coupon;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.NotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
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


  Optional<Coupon> findByName(String name);

  default Coupon findByNameOrElseThrow(String name) {
    Coupon coupon = findByName(name).orElseThrow(
        () -> new NotFoundException(CouponErrorCode.COUPON_NOT_FOUND));
    return coupon;
  }
}
