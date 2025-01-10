package com.example.playcation.tag.repository;

import com.example.playcation.common.PagingDto;
import com.example.playcation.tag.Dto.CreatedTagResponseDto;
import org.springframework.data.domain.PageRequest;

public interface TagRepositoryCustom {

  PagingDto<CreatedTagResponseDto> searchTags(PageRequest pageRequest);
}
