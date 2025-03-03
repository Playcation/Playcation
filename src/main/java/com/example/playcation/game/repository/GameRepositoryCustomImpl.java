package com.example.playcation.game.repository;

import com.example.playcation.game.dto.PagingGameResponseDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.entity.QGame;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRepositoryCustomImpl implements GameRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public PagingGameResponseDto searchGames(Pageable pageable, String title, Long categoryId,
      BigDecimal price, LocalDate createdAt) {
    QGame game = QGame.game;

    List<Game> gameList = queryFactory
        .selectFrom(game)
        .where(
            eqTitle(title),
            eqCategory(categoryId),
            gtPrice(price),
            afterCreatedAt(createdAt)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long count = queryFactory
        .select(game.count())
        .from(game)
        .where(
            eqTitle(title),
            eqCategory(categoryId),
            gtPrice(price),
            afterCreatedAt(createdAt)
        )
        .fetchOne();

    return new PagingGameResponseDto(gameList, count);
  }

  private BooleanExpression eqTitle(String title) {
    return title != null ? QGame.game.title.like("%" + title + "%") : null;
  }

  private BooleanExpression eqCategory(Long categoryId) {
    return categoryId != null ? QGame.game.category.id.eq(categoryId) : null;
  }

  private BooleanExpression gtPrice(BigDecimal price) {
    return price != null ? QGame.game.price.gt(price) : null;
  }

  private BooleanExpression afterCreatedAt(LocalDate createdAt) {
    return createdAt != null ? QGame.game.createdAt.after(createdAt.atStartOfDay()) : null;
  }
}
