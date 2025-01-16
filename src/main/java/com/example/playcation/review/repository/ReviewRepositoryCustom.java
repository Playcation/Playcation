package com.example.playcation.review.repository;

import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.review.dto.CreatedReviewResponseDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

  PagingDto<CreatedReviewResponseDto> searchReviews(Pageable pageable, Long gameId, Long userId,
      ReviewStatus rating);

}
