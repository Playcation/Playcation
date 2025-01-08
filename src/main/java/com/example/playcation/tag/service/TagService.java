package com.example.playcation.tag.service;

import com.example.playcation.tag.Dto.CreatedTagRequestDto;
import com.example.playcation.tag.Dto.CreatedTagResponseDto;
import com.example.playcation.tag.entity.Tag;
import com.example.playcation.tag.repository.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {


  private final TagRepository tagRepository;

  public CreatedTagResponseDto CreateTag(CreatedTagRequestDto requestDto) {

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

  public List<CreatedTagResponseDto> findAllTag(int page) {

    PageRequest pageRequest = PageRequest.of(page, 10);

    return tagRepository.findAll(pageRequest).stream().map(CreatedTagResponseDto::toDto).toList();
  }

  public CreatedTagResponseDto updateTag(Long tagId, CreatedTagRequestDto requestDto) {
    Tag tag = tagRepository.findByIdOrElseThrow(tagId);

    tag.updateTag(requestDto);

    tagRepository.save(tag);

    return CreatedTagResponseDto.toDto(tag);
  }

  public void deleteTag(Long tagId) {
    Tag tag = tagRepository.findByIdOrElseThrow(tagId);

    tagRepository.delete(tag);
  }
}
