package com.example.playcation.s3.repository;

import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.s3.entity.FileDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDetailRepository extends JpaRepository<FileDetail, Long> {

  Optional<FileDetail> findByFileName(String fileName);
  default FileDetail findByFileNameOrElseThrow(String fileName){
    FileDetail fileDetail = findByFileName(fileName).orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));
    return fileDetail;
  }
}
