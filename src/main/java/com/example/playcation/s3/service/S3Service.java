package com.example.playcation.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.playcation.exception.FileErrorCode;
import com.example.playcation.exception.InternalServerException;
import com.example.playcation.s3.dto.FileResponseDto;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.entity.GameFile;
import com.example.playcation.s3.repository.FileDetailRepository;
import com.example.playcation.s3.repository.GameFileRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class S3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final FileDetailRepository fileDetailRepository;
  private final GameFileRepository gameFileRepository;
  private final AmazonS3 s3;

  /**
   * S3에 파일 업로드하는 메서드
   *
   * @param multipartFile
   * @return 파일이 업로드된 URL을 반환합니다.
   */
  @Transactional
  public FileDetail uploadFile(MultipartFile multipartFile){
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
      throw new InternalServerException(FileErrorCode.FAIL_UPLOAD_FILE);
    }
    String filePath = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;

    // 파일이름은 사용자가 지정한 파일이름으로 저장합니다.
    FileDetail attachFile = new FileDetail(bucket,
        multipartFile.getOriginalFilename(),
        fileName,
        filePath,
        multipartFile.getSize(),
        multipartFile.getContentType()
    );
    FileDetail fileDetail = fileDetailRepository.save(attachFile);

    return fileDetail;
  }

  /**
   * S3에서 파일 삭제
   * 
   * @param filePath ( 파일 경로 )
   */
  @Transactional
  public void deleteFile(String filePath){
    String fileName = pathToFileName(filePath);
    s3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    FileDetail attachFile = fileDetailRepository.findByServerFileNameOrElseThrow(fileName);
    fileDetailRepository.delete(attachFile);
  }

  // 파일명을 난수화하기 위해 UUID 를 활용하여 난수를 돌린다.
  public String createFileName(String fileName){
    return LocalDateTime.now() + "_" + UUID.randomUUID().toString().concat(getFileExtension(fileName));
  }

  //  "."의 존재 유무만 판단
  private String getFileExtension(String fileName){
    try{
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
    }
  }

  /**
   * 다중 파일 업로드
   *
   * @param files
   * @return 첨부파일 URL List
   */
  @Async
  @Transactional
  public CompletableFuture<List<String>> uploadFiles(List<MultipartFile> files) {
    List<String> filePaths = new ArrayList<>();
    for (MultipartFile file : files) {
      filePaths.add(uploadFile(file).getFilePath());
    }
    return CompletableFuture.completedFuture(filePaths);
  }

  @Transactional
  public FileResponseDto getObjectByGameId(Long gameId) throws IOException{
    
    // 게임 아디디를 통해 게임파일을 가져오는 메소드
    GameFile gameFile = gameFileRepository.findByIdOrElseThrow(gameId);
    FileDetail fileDetail = fileDetailRepository.findByIdOrElseThrow(gameFile.getFileDetail().getId());
    
    // S3에서 파일을 가져오는 메소드
    S3Object o = s3.getObject(new GetObjectRequest(bucket, fileDetail.getServerFileName()));
    S3ObjectInputStream objectInputStream = o.getObjectContent();
    byte[] bytes = IOUtils.toByteArray(objectInputStream);

//    FileDetail attachFile = fileDetailRepository.findByFileNameOrElseThrow(storedFileName);

    return new FileResponseDto(bytes, fileDetail.getOriginFileName());
  }

  // URL에서 파일 이름 가져오는 함수
  public String pathToFileName(String filePath){
    return filePath.substring(filePath.lastIndexOf(".com/") + 5);
  }
}
