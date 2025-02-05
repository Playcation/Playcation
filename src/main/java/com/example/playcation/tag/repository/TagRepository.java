package com.example.playcation.tag.repository;

import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.TagErrorCode;
import com.example.playcation.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TagRepository extends JpaRepository<Tag, Long>, TagRepositoryCustom {
  default Tag findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(TagErrorCode.TAG_NOT_FOUND));
  }


  boolean existsByTagName(String  tagId);
}
