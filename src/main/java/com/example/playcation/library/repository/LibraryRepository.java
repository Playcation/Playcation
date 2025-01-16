package com.example.playcation.library.repository;

import com.example.playcation.exception.LibraryErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.library.entity.Library;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface LibraryRepository extends JpaRepository<Library, Long>, LibraryRepositoryCustom {
  default Library findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(LibraryErrorCode.NOT_FOUND_LIBRARY));
  }

  // 해당 게임 id를 가진 라이브러리를 불러옴
  List<Library> findLibraryByGameId(Long gameId);

  // 특정 사용자가 특정 게임을 소유하고 있는지
  Optional<Library> findByUserIdAndGameId(Long userId, Long gameId);
}
