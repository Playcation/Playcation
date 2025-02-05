package com.example.playcation.library.repository;

import com.example.playcation.library.dto.LibraryListResponseDto;
import com.example.playcation.user.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface LibraryRepositoryCustom {
  LibraryListResponseDto findLibraryByUserId(Pageable pageable, User user);
}
