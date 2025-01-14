package com.example.playcation.gametag.repository;

import static com.example.playcation.game.entity.QGame.game;

import com.example.playcation.gametag.dto.GameTagListResponseDto;
import com.example.playcation.gametag.entity.GameTag;
import com.example.playcation.gametag.entity.QGameTag;
import com.example.playcation.tag.entity.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameTagRepositoryCustomImpl implements GameTagRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public GameTagListResponseDto findGameTagByTag(Pageable pageable, Tag tag) {
    QGameTag gameTag = QGameTag.gameTag;

    List<GameTag> gameTagList = queryFactory
        .selectFrom(gameTag)
        .join(gameTag.game, game).fetchJoin()
        .where(gameTag.tag.eq(tag))
        .orderBy(game.updatedAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long count = queryFactory
        .select(gameTag.count())
        .from(gameTag)
        .where(
            gameTag.tag.eq(tag)
        )
        .fetchOne();
    return new GameTagListResponseDto(gameTagList, count);
  }
}

