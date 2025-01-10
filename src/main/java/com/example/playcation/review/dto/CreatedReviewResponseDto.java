package com.example.playcation.review.dto;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.review.entity.Review;
import lombok.Getter;

@Getter
public class CreatedReviewResponseDto extends BaseEntity {

  private Long reviewId;

  private Long userId;

  private String content;

  private ReviewStatus rating;

  private Long likeCount;

  public CreatedReviewResponseDto(Long reviewId, Long userId, String content,
      ReviewStatus rating, Long likeCount) {
    this.reviewId = reviewId;
    this.userId = userId;
    this.content = content;
    this.rating = rating;
    this.likeCount = likeCount;
  }

  public static CreatedReviewResponseDto toDto(Review review) {
    return new CreatedReviewResponseDto(
        review.getId(),
        review.getLibrary().getUser().getId(),  // library에서 user 정보를 가져와 userId 사용
        review.getContent(),
        review.getRating(),
        review.getCountLike()
    );
  }
}
