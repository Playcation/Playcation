package com.example.playcation.review.dto;

import com.example.playcation.enums.ReviewStatus;
import lombok.Getter;

@Getter
public class UpdatedReviewRequestDto {
  private String content;

  private ReviewStatus rating;

  public UpdatedReviewRequestDto(String content, ReviewStatus rating) {
    this.content = content;
    this.rating = rating;
  }
}
