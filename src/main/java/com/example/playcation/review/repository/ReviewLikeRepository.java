package com.example.playcation.review.repository;

import com.example.playcation.review.entity.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<Like, Long> {

  boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

  Like findByUserIdAndReviewId(Long userId, Long reviewId);
}
