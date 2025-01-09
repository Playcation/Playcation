package com.example.playcation.library.repository;

import com.example.playcation.exception.LibraryErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.library.entity.Library;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface LibraryRepository extends JpaRepository<Library, Long>, LibraryRepositoryCustom {
  default Library findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(LibraryErrorCode.NOT_FOUND_LIBRARY));
  }

}
