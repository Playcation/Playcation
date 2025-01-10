package com.example.playcation.tag.repository;

import com.example.playcation.common.PagingDto;
import com.example.playcation.tag.Dto.CreatedTagResponseDto;
import com.example.playcation.tag.entity.QTag;
import com.example.playcation.tag.entity.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TagRepositoryCustomImpl implements TagRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public PagingDto<CreatedTagResponseDto> searchTags(PageRequest pageRequest) {
    QTag tag = QTag.tag;

    List<Tag> tagList = queryFactory
        .selectFrom(tag)
        .offset(pageRequest.getOffset())
        .limit(pageRequest.getPageSize())
        .fetch();

    Long count = queryFactory
        .select(tag.count())
        .from(tag)
        .where()
        .fetchOne();

    List<CreatedTagResponseDto> list = tagList.stream().map(CreatedTagResponseDto::toDto).toList();
    return new PagingDto<>(list, count);
  }
}
