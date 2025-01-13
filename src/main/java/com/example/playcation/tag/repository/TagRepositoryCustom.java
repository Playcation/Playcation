package com.example.playcation.tag.repository;

import com.example.playcation.common.PagingDto;
import com.example.playcation.tag.Dto.CreatedTagResponseDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface TagRepositoryCustom {

  PagingDto<CreatedTagResponseDto> searchTags(Pageable pageable);
}
