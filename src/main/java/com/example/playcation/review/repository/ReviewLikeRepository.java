package com.example.playcation.review.repository;

import com.example.playcation.review.entity.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewLikeRepository extends JpaRepository<Like, Long> {

  boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

  Like findByUserIdAndReviewId(Long userId, Long reviewId);

  @Transactional // JPA 삭제 쿼리 실행 시 트랜잭션이 필요함
  @Modifying  // DELETE 쿼리 실행을 명시적으로 허용
  @Query("DELETE FROM Like l WHERE l.review.id = :reviewId")
  void deleteByReviewId(Long reviewId);
}
