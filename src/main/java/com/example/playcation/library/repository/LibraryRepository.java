package com.example.playcation.library.repository;

import com.example.playcation.exception.LibraryErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.library.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {
  default Library findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(LibraryErrorCode.NOT_FOUND_LIBRARY));
  }

}
