package com.example.playcation.s3.repository;

import com.example.playcation.exception.FileErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.s3.entity.UserFile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFileRepository extends JpaRepository<UserFile, Long> {

  Optional<UserFile> findByUserId(Long userId);
  default UserFile findByUserIdOrElseThrow(Long userId) {
    return findByUserId(userId).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
  }

  void deleteByUserId(Long userId);

  void deleteByUserIdAndFileDetailId(Long userId, Long fileDetailId);
}
