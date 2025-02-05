package com.example.playcation.user.repository;

import com.example.playcation.batch.PointWithUserDto;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.PaymentErrorCode;
import com.example.playcation.user.entity.Point;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointRepository extends JpaRepository<Point, Long> {

  Optional<Point> getPointByUserId(Long userId);

  default Point getPointByUserIdOrElseThrow(Long id) {
    return getPointByUserId(id).orElseThrow(
        () -> new NotFoundException(PaymentErrorCode.NOT_FOUND_POINT));
  }

  @Query("SELECT new com.example.playcation.batch.PointWithUserDto(p, p.user) FROM Point p WHERE p.user.name IS NOT NULL")
  Page<PointWithUserDto> findAllPointAndUser(Pageable pageable);
}
