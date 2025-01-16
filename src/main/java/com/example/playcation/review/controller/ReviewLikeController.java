package com.example.playcation.review.controller;


import com.example.playcation.common.TokenSettings;
import com.example.playcation.review.dto.CreatedReviewRequestDto;
import com.example.playcation.review.dto.CreatedReviewResponseDto;
import com.example.playcation.review.dto.ReviewLikeResponseDto;
import com.example.playcation.review.service.ReviewLikeService;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/reviews/{reviewId}/likes")
@RestController
@RequiredArgsConstructor
public class ReviewLikeController {

  private final ReviewLikeService reviewLikeService;
  private final JWTUtil jwtUtil;


  // 좋아요
  @PostMapping
  public ResponseEntity<String> createReview(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable Long reviewId) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    reviewLikeService.createReviewLike(userId, reviewId);
    return new ResponseEntity<>("좋아요", HttpStatus.CREATED);
  }

  // 좋아요 해제
  @DeleteMapping
  public ResponseEntity<String> deleteReview(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable Long reviewId) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    reviewLikeService.deleteReview(userId, reviewId);
    return new ResponseEntity<>("좋아요 취소", HttpStatus.OK);
  }

}
