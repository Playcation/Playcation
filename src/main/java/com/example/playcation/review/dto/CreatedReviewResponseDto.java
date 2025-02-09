package com.example.playcation.review.dto;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.review.entity.Review;
import lombok.Getter;

@Getter
public class CreatedReviewResponseDto extends BaseEntity {

  private Long reviewId;

  private Long userId; // librarydptj user 정보를 가져와 UserId로 사용

  private String userName; // 유저 닉네임

  private String userImage; // 유저 프로필

  private String content;

  private ReviewStatus rating;

  private Long likeCount;

  public CreatedReviewResponseDto(Long reviewId, Long userId, String userName, String userImage, String content,
      ReviewStatus rating, Long likeCount) {
    this.reviewId = reviewId;
    this.userId = userId;
    this.userName = userName;
    this.userImage = userImage;
    this.content = content;
    this.rating = rating;
    this.likeCount = likeCount;
  }

  public static CreatedReviewResponseDto toDto(Review review, String userName, String userImage) {
    return new CreatedReviewResponseDto(
        review.getId(),
        review.getLibrary().getUser().getId(),  // library에서 user 정보를 가져와 userId 사용
        userName,
        userImage,
        review.getContent(),
        review.getRating(),
        review.getCountLike()
    );
  }
}
