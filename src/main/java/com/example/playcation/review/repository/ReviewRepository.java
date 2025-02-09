package com.example.playcation.review.repository;

import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.ReviewErrorCode;
import com.example.playcation.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

  boolean existsByLibraryId(Long libraryId);

  default Review findByIdOrElseThrow(Long reviewId) {
    return findById(reviewId)
        .orElseThrow(() -> new NotFoundException(ReviewErrorCode.NOT_FOUND_REVIEW));
  }

  List<Review> findReviewByGameId(Long id);

}
