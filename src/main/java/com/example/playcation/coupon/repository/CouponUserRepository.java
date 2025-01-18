package com.example.playcation.coupon.repository;

import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {

  default CouponUser findByIdOrElseThrow(Long id) {
    CouponUser couponUser = findById(id).orElseThrow(
        () -> new NotFoundException(CouponErrorCode.COUPON_NOT_FOUND));

    return couponUser;
  }

  Optional<CouponUser> findByUserIdAndCouponId(Long userId, Long couponId);

  Page<CouponUser> findAllByUserId(Long userId, Pageable pageable);

  List<CouponUser> findAllByExpiredDateIsBefore(LocalDate date);
}
