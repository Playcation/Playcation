package com.example.playcation.review.repository;

import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.review.dto.PagingReviewResponseDto;
import org.springframework.data.domain.PageRequest;

public interface ReviewRepositoryCustom {

  PagingReviewResponseDto searchReviews(PageRequest pageRequest, Long gameId, Long userId, ReviewStatus rating);

}
