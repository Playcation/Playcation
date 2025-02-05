package com.example.playcation.tag.service;

import com.amazonaws.services.kms.model.TagException;
import com.example.playcation.common.PagingDto;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.TagErrorCode;
import com.example.playcation.tag.Dto.CreatedTagRequestDto;
import com.example.playcation.tag.Dto.CreatedTagResponseDto;
import com.example.playcation.tag.entity.Tag;
import com.example.playcation.tag.repository.TagRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {


  private final TagRepository tagRepository;

  @Transactional
  public CreatedTagResponseDto CreateTag(CreatedTagRequestDto requestDto) {

    if (tagRepository.existsByTagName(requestDto.getTagName())) {
      throw new DuplicatedException(TagErrorCode.DUPLICATE_TAG);
    }
    Tag tag = Tag.builder()
        .tagName(requestDto.getTagName())
        .build();

    tagRepository.save(tag);
    return CreatedTagResponseDto.toDto(tag);
  }

  public CreatedTagResponseDto findTag(Long tagId) {

    Tag tag = tagRepository.findByIdOrElseThrow(tagId);

    return CreatedTagResponseDto.toDto(tag);
  }

  public PagingDto<CreatedTagResponseDto> findAllTag(int page) {

    Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));

    return tagRepository.searchTags(pageable);
  }

  @Transactional
  public CreatedTagResponseDto updateTag(Long tagId, CreatedTagRequestDto requestDto) {
    Tag tag = tagRepository.findByIdOrElseThrow(tagId);

    tag.updateTag(requestDto);

    tagRepository.save(tag);

    return CreatedTagResponseDto.toDto(tag);
  }

  @Transactional
  public void deleteTag(Long tagId) {
    Tag tag = tagRepository.findByIdOrElseThrow(tagId);

    tagRepository.delete(tag);
  }
}
