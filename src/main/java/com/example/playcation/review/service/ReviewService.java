package com.example.playcation.review.service;

import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.ReviewErrorCode;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.repository.LibraryRepository;
import com.example.playcation.review.dto.CreatedReviewRequestDto;
import com.example.playcation.review.dto.CreatedReviewResponseDto;
import com.example.playcation.review.dto.PagingReviewResponseDto;
import com.example.playcation.review.dto.UpdatedReviewRequestDto;
import com.example.playcation.review.entity.Review;
import com.example.playcation.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

  public final ReviewRepository reviewRepository;
  public final GameRepository gameRepository;
  public final LibraryRepository libraryRepository;

  // 리뷰 생성
  @Transactional
  public CreatedReviewResponseDto createReview(Long userId, Long gameId,
      CreatedReviewRequestDto reviewRequestDto) {
    // 게임 조회 및 예외처리
    Game game = gameRepository.findByIdOrElseThrow(gameId);

    // 특정 사용자가 게임을 소유하고 있는지 확인
    Library library = libraryRepository.findByUserIdAndGameId(userId, gameId)
        .orElseThrow(() -> new NotFoundException(ReviewErrorCode.GAME_NOT_IN_LIBRARY));

    // 중복 리뷰 작성 여부 검증
    boolean reviewExists = reviewRepository.existsByLibraryId(library.getId());
    if (reviewExists) {
      throw new DuplicatedException(ReviewErrorCode.REVIEW_EXIST);
    }

    Review review = reviewRepository.save( Review.builder()
        .game(game) // 리뷰가 어떤 게임에 대한 것인지
        .library(library)
        .content(reviewRequestDto.getContent())
        .rating(reviewRequestDto.getRating())
        .countLike(0L)  // 기본 좋아요 수는 0으로 설정
        .build());
    return CreatedReviewResponseDto.toDto(review);
  }


  // 리뷰 조회
  public PagingReviewResponseDto searchReviews(int page,int size ,Long gameId,Long userId,
      ReviewStatus rating){
    // 게임 조회 및 예외처리
    Game game = gameRepository.findByIdOrElseThrow(gameId);

    // 페이징시 최대 출력 갯수와 정렬조건 설정 ( 한 페이지당 리뷰 개수는 5개 )
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.DESC,"createdAt"));
    return reviewRepository.searchReviews(pageRequest,gameId, userId,rating);
  }


  // 리뷰 수정
  @Transactional
  public CreatedReviewResponseDto updateReview(Long userId,Long gameId, Long reviewId, UpdatedReviewRequestDto updateRequest){
    // 게임이 존재하는지, 리뷰가 있는지
    // boolean exists = gameRepository.existsById(gameId);
    Game game = gameRepository.findByIdOrElseThrow(gameId);
    Review review = reviewRepository.findByIdOrElseThrow(reviewId);

    // 본인이 작성한 리뷰인지 확인
    if (!(review.getLibrary().getUser().getId().equals(userId))) {
      throw new NoAuthorizedException(ReviewErrorCode.NOT_AUTHOR_OF_REVIEW);
    }

    // 수정된 내용 반영
    review.updateContent(updateRequest.getContent(), updateRequest.getRating());
    return CreatedReviewResponseDto.toDto(review);
  }


  // 리뷰 삭제
  @Transactional
  public void deleteReview(Long userId, Long gameId, Long reviewId){
    // 게임이 존재하는지, 리뷰가 있는지
//    boolean exists = gameRepository.existsById(gameId);
    Game game = gameRepository.findByIdOrElseThrow(gameId);
    Review review = reviewRepository.findByIdOrElseThrow(reviewId);

    // 본인이 작성한 리뷰인지 확인
    if (!(review.getLibrary().getUser().getId().equals(userId))) {
      throw new NoAuthorizedException(ReviewErrorCode.NOT_AUTHOR_OF_REVIEW);
    }
    reviewRepository.delete(review);
  }
}


