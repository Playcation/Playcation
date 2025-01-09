package com.example.playcation.s3.repository;

import com.example.playcation.exception.FileErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.s3.entity.FileDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface FileDetailRepository extends JpaRepository<FileDetail, Long> {

  Optional<FileDetail> findByOriginFileName(String fileName);
  default FileDetail findByFileNameOrElseThrow(String fileName){
    FileDetail fileDetail = findByOriginFileName(fileName).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
    return fileDetail;
  }

  default FileDetail findByIdOrElseThrow(Long id){
    FileDetail fileDetail = findById(id).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
    return fileDetail;
  }

  Optional<FileDetail> findByServerFileName(String fileName);
  default FileDetail findByServerFileNameOrElseThrow(String fileName){
    FileDetail fileDetail = findByServerFileName(fileName).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
    return fileDetail;
  }
}
