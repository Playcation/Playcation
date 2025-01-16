package com.example.playcation.review.dto;

import com.example.playcation.enums.ReviewStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreatedReviewRequestDto {

  @NotEmpty(message = "내용은 필수 입력 값입니다.")
  private String content;

  @NotNull(message = "평가는 필수 입력 값입니다.")
  private ReviewStatus rating;


  public CreatedReviewRequestDto(String content, ReviewStatus rating) {
    this.content = content;
    this.rating = rating;
  }

}
