package com.example.playcation.game.repository;

import com.example.playcation.game.dto.PagingGameResponseDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.entity.QGame;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRepositoryCustomImpl implements GameRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public PagingGameResponseDto searchGames(PageRequest pageRequest, String title, String category,
      BigDecimal price, LocalDateTime createdAt) {
    QGame game = QGame.game;

    List<Game> gameList = queryFactory
        .selectFrom(game)
        .where(
            eqTitle(title),
            eqCategory(category),
            eqPrice(price),
            eqCreatedAt(createdAt)
        )
        .offset(pageRequest.getOffset())
        .limit(pageRequest.getPageSize())
        .fetch();

    Long count = queryFactory
        .select(game.count())
        .from(game)
        .where(
            eqTitle(title),
            eqCategory(category),
            eqPrice(price),
            eqCreatedAt(createdAt)
        )
        .fetchOne();
    return new PagingGameResponseDto(gameList, count);
  }

  private BooleanExpression eqTitle(String title) {
    return title != null ? QGame.game.title.eq(title) : null;
  }

  private BooleanExpression eqCategory(String category) {
    return category != null ? QGame.game.category.eq(category) : null;
  }

  private BooleanExpression eqPrice(BigDecimal price) {
    return price != null ? QGame.game.price.eq(price) : null;
  }

  private BooleanExpression eqCreatedAt(LocalDateTime createdAt) {
    return createdAt != null ? QGame.game.createdAt.before(createdAt) : null;
  }
}
