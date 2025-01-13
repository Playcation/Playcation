package com.example.playcation.review.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.review.dto.CreatedReviewRequestDto;
import com.example.playcation.review.dto.CreatedReviewResponseDto;
import com.example.playcation.review.dto.UpdatedReviewRequestDto;
import com.example.playcation.review.service.ReviewService;
import com.example.playcation.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games/{gameId}/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final JWTUtil jwtUtil;

  // 리뷰 생성
  @PostMapping
  public ResponseEntity<CreatedReviewResponseDto> createReview(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable Long gameId,
      @Valid @RequestBody CreatedReviewRequestDto reviewRequestDto) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    CreatedReviewResponseDto reviewResponseDto = reviewService.createReview(userId, gameId,
        reviewRequestDto);
    return new ResponseEntity<>(reviewResponseDto, HttpStatus.CREATED);
  }

  // 리뷰 조회
  @GetMapping
  public ResponseEntity<PagingDto<CreatedReviewResponseDto>> findReviewsAndPaging(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @PathVariable Long gameId,
      @RequestParam(required = false) ReviewStatus rating // 필터링할 상태(긍정적/부정적)
  ) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    PagingDto<CreatedReviewResponseDto> reviews = reviewService.searchReviews(page,size,gameId,userId,rating);
    return new ResponseEntity<>(reviews, HttpStatus.OK);
  }

  // 리뷰 수정
  @PatchMapping("/{reviewId}")
  public ResponseEntity<CreatedReviewResponseDto> updateReview(
      @RequestHeader("Authorization") String authorizationHeader,
      @PathVariable Long gameId,
      @PathVariable Long reviewId,
      @Valid @RequestBody UpdatedReviewRequestDto updateRequest
  ){
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    CreatedReviewResponseDto updatedReview = reviewService.updateReview(userId, gameId, reviewId, updateRequest);
    return new ResponseEntity<>(updatedReview, HttpStatus.OK);
  }


  // 리뷰 삭제
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<String> deleteReview(
      @PathVariable Long gameId, @PathVariable Long reviewId,
      @RequestHeader("Authorization") String authorizationHeader) {

    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    reviewService.deleteReview(userId, gameId, reviewId);
    return new ResponseEntity<>("리뷰가 삭제되었습니다.", HttpStatus.OK);
  }

}
