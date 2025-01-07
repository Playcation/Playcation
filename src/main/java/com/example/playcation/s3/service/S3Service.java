package com.example.playcation.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.repository.FileDetailRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class S3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final FileDetailRepository fileDetailRepository;
  private final AmazonS3 s3;

  @Transactional
  public String uploadFile(MultipartFile multipartFile){
    if (multipartFile == null || multipartFile.isEmpty()) {
      return null;
    }
    String fileName = createFileName(multipartFile.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(multipartFile.getSize());
    objectMetadata.setContentType(multipartFile.getContentType());

    try(InputStream inputStream = multipartFile.getInputStream()){
      s3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
          .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
    }
    String filePath = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
    FileDetail attachFile = new FileDetail(bucket, fileName, filePath, multipartFile.getSize(), multipartFile.getContentType());
    fileDetailRepository.save(attachFile);

    return attachFile.getFilePath();
  }

  public FileDetail findByFileNameOrElseThrow(String fileName) {
    FileDetail attachFile = fileDetailRepository.findByFileNameOrElseThrow(fileName);
    return attachFile;
  }

  @Transactional
  public void deleteFile(String fileName){
    s3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    FileDetail attachFile = findByFileNameOrElseThrow(fileName);
    fileDetailRepository.delete(attachFile);
  }

  // 파일명을 난수화하기 위해 UUID 를 활용하여 난수를 돌린다.
  public String createFileName(String fileName){
    return LocalDateTime.now() + UUID.randomUUID().toString().concat(getFileExtension(fileName));
  }

  //  "."의 존재 유무만 판단
  private String getFileExtension(String fileName){
    try{
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
    }
  }

  public List<String> uploadFiles(List<MultipartFile> files) {
    List<String> filePaths = new ArrayList<>();
    for (MultipartFile file : files) {
      filePaths.add(uploadFile(file));
    }
    return filePaths;
  }
}
