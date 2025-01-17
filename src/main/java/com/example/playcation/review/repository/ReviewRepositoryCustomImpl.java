package com.example.playcation.review.repository;

import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.game.entity.QGame;
import com.example.playcation.library.entity.QLibrary;
import com.example.playcation.review.dto.CreatedReviewResponseDto;
import com.example.playcation.review.entity.QReview;
import com.example.playcation.review.entity.Review;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public PagingDto<CreatedReviewResponseDto> searchReviews(Pageable pageable, Long gameId,
      Long userId, ReviewStatus rating) {

    QReview review = QReview.review;
    QGame game = QGame.game;
    QLibrary library = QLibrary.library;

    // 리뷰 리스트 조회
    List<Review> reviewList = queryFactory
        .selectFrom(review)
        .join(review.game, game) // 리뷰 조회할 때, 어떤 게임에 대한 리뷰인지 알기 위해
        .join(review.library, library) // userId를 라이브러리에서 가져오기 위해
        .where(
            eqGame(gameId), // 게임 id가 주어진 값과 일치하는지 체크(특정 게임에 대해 리뷰 조회)
            eqRating(rating) // 긍정적,부정적 필터링
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(review.createdAt.desc())
        .fetch();

    // 게임의 전체 리뷰의 개수
    Long count = queryFactory
        .select(review.count())
        .from(review)
        .join(review.game, game)
        .join(review.library, library)
        .where(
            eqGame(gameId),
            eqRating(rating)
        )
        .fetchOne();

    List<CreatedReviewResponseDto> list = reviewList.stream()
        .map(CreatedReviewResponseDto::toDto)
        .toList();
    return new PagingDto<>(list, count);
  }

  // 라이브러리에서 유저 ID를 필터링
  private BooleanExpression eqUser(Long userId) {
    return userId != null ? QLibrary.library.user.id.eq(userId) : null;
  }

  // 게임 ID 필터링
  private BooleanExpression eqGame(Long gameId) {
    return gameId != null ? QGame.game.id.eq(gameId) : null;
  }

  // 리뷰 상태 필터링(긍정적, 부정적)
  private BooleanExpression eqRating(ReviewStatus rating) {
    return rating != null ? QReview.review.rating.eq(rating) : null;
  }
}
