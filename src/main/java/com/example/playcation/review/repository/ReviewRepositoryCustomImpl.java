package com.example.playcation.review.repository;

import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.ReviewStatus;
import com.example.playcation.game.entity.QGame;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.entity.QLibrary;
import com.example.playcation.review.dto.CreatedReviewResponseDto;
import com.example.playcation.review.entity.QReview;
import com.example.playcation.review.entity.Review;
import com.example.playcation.user.entity.QUser;
import com.example.playcation.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
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
    QUser user = QUser.user;

    // 리뷰 리스트 조회
    List<Review> reviewList = queryFactory
        .selectFrom(review)
        .join(review.game, game) // 리뷰 조회할 때, 어떤 게임에 대한 리뷰인지 알기 위해
        .join(review.library, library) // 이 리뷰가 속한 라이브러리
        .where(
            eqGame(gameId), // 게임 id가 주어진 값과 일치하는지 체크(특정 게임에 대해 리뷰 조회)
            eqRating(rating) // 긍정적,부정적 필터링
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(review.createdAt.asc())
        .fetch();


    // 라이브러리에 해당하는 유저아이디 가져오기
    List<Long> libraryIds = reviewList.stream()
        .map(r -> r.getLibrary().getId())
        .toList();

    List<Library> libraryInfos = queryFactory
        .selectFrom(library)
        .where(
            library.id.in(libraryIds)
        ) 
        .fetch();


    List<Long> userIds = libraryInfos.stream().map(
            library1 -> library1.getUser().getId()
        )
        .toList();

    List<User> userInfo = queryFactory
        .selectFrom(user)
        .where(
            user.id.in(userIds)
        )
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

    List<CreatedReviewResponseDto> list = IntStream.range(0, reviewList.size())
        .mapToObj(i -> new CreatedReviewResponseDto(
            reviewList.get(i).getId(),
            i < userInfo.size() ? userInfo.get(i).getId() : null,  // 리스트 크기 체크
            i < userInfo.size() ? userInfo.get(i).getUsername() : "Unknown",
            i < userInfo.size() ? userInfo.get(i).getImageUrl() : null,
            reviewList.get(i).getContent(),
            reviewList.get(i).getRating(),
            reviewList.get(i).getCountLike()
        ))
        .collect(Collectors.toList());
    return new PagingDto<>(list, count);
  }

  // 라이브러리에서 유저 ID를 필터링
  private BooleanExpression eqUser(Long userId) {
    return userId != null ? QLibrary.library.user.id.eq(userId) : null;
  }

  private BooleanExpression inLibrary(List<Long> libraryId) {
    return libraryId != null ? QLibrary.library.id.in(libraryId) : null;
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
