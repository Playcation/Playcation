package com.example.playcation.review.service;

import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.ReviewErrorCode;
import com.example.playcation.review.entity.Like;
import com.example.playcation.review.entity.Review;
import com.example.playcation.review.repository.ReviewLikeRepository;
import com.example.playcation.review.repository.ReviewRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewLikeRepository reviewLikeRepository;

  /**
   * 리뷰 좋아요 추가
   */
  public void createReviewLike(Long userId, Long reviewId) {
    // 리뷰, 유저 존재 여부 확인
    Review review = reviewRepository.findByIdOrElseThrow(reviewId);
    User user = userRepository.findByIdOrElseThrow(userId);

    // 이미 좋아요를 누른 경우 예외 처리
    boolean alreadyLiked = reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId);
    if (alreadyLiked) {
      throw new DuplicatedException(ReviewErrorCode.ALREADY_REVEIW_LIKE);
    }

    // 좋아요 생성 및 저장
    Like like = new Like(review, user);
    reviewLikeRepository.save(like);

    // 리뷰 좋아요 수 증가
    review.addLike();
    reviewRepository.save(review);
  }

  /**
   * 리뷰 좋아요 취소
   */
  public void deleteReview(Long userId, Long reviewId) {
    // 리뷰, 유저 존재 여부 확인
    Review review = reviewRepository.findByIdOrElseThrow(reviewId);
    userRepository.findByIdOrElseThrow(userId);

    // 리뷰 좋아요 기록 조회
    Like like = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId);
    if (like == null) {
      throw new NotFoundException(ReviewErrorCode.LIKE_NOT_FOUND);
    }

    // 좋아요 기록 삭제
    reviewLikeRepository.delete(like);

    // 리뷰 좋아요 수 감소
    review.removeLike();
    reviewRepository.save(review);
  }
}

