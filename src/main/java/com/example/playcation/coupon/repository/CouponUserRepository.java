package com.example.playcation.coupon.repository;

import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.exception.CouponErrorCode;
import com.example.playcation.exception.NotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {

  default CouponUser findByIdOrElseThrow(Long id) {
    CouponUser couponUser = findById(id).orElseThrow(
        () -> new NotFoundException(CouponErrorCode.COUPON_NOT_FOUND));

    return couponUser;
  }

  Optional<CouponUser> findByUserIdAndCouponId(Long userId, Long couponId);

  boolean existsByUserIdAndCouponName(Long userId, String couponName);

  Page<CouponUser> findAllByUserId(Long userId, Pageable pageable);

  /**
   * 유효 기간 지난 쿠폰 찾기
   *
   * @param date 현재 날짜
   * @apiNote {com.example.playcation.batch.job.ExpiredCouponJob} 배치 실행용
   */
  Page<CouponUser> findAllByExpiredDateIsBefore(LocalDate date, Pageable pageable);
}
