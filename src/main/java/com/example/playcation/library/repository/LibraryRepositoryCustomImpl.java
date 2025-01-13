package com.example.playcation.library.repository;

import static com.example.playcation.game.entity.QGame.game;

import com.example.playcation.library.dto.LibraryListResponseDto;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.entity.QLibrary;
import com.example.playcation.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LibraryRepositoryCustomImpl implements LibraryRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public LibraryListResponseDto findLibraryByUserId(Pageable pageable, User user) {
    QLibrary library = QLibrary.library;

    List<Library> libraryList = queryFactory
        .selectFrom(library)
        .join(library.game, game).fetchJoin()
        .where(library.user.eq(user))
        .orderBy(library.favourite.desc(), game.updatedAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long count = queryFactory
        .select(library.count())
        .from(library)
        .where(library.user.eq(user))
        .fetchOne();
    return new LibraryListResponseDto(libraryList, count);
  }
}
