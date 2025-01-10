package com.example.playcation.review.dto;

import com.example.playcation.review.entity.Review;
import java.util.List;
import lombok.Getter;

@Getter
public class PagingReviewResponseDto {

  // 해당 조건을 만족하는 리뷰 목록
  private List<CreatedReviewResponseDto> reviewList;

  // 전체 리뷰 개수
  private Long count;

  public PagingReviewResponseDto(List<Review> reviewList, Long count) {
    // 외부에서 들어온 List<Review>를 CreatedReviewResponseDto로 변환 후 List형식으로 저장
    this.reviewList = reviewList.stream()
        .map(CreatedReviewResponseDto::toDto)
        .toList(); // 변환된 리스트 저장
    this.count = count; // 전체 리뷰 개수 저장
  }
}
