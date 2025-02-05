package com.example.playcation.s3.service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.repository.FileDetailRepository;
import com.example.playcation.s3.service.S3Service;
import jakarta.transaction.Transactional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class S3ServiceIntegrationTest {

  @Value("${cloud.aws.s3.bucket}")
  private String imageBucket;

  @Value("${cloud.aws.s3.bucket.game}")
  private String gameBucket;

  @Autowired
  private FileDetailRepository fileDetailRepository;

  @MockitoBean
  private AmazonS3 s3;

  @Autowired
  private S3Service s3Service;

  private MultipartFile image;
  private MultipartFile zip;

  @BeforeEach
  void setUp(){
    image = new MockMultipartFile("image.jpeg",
        "original_image_file.jpeg",
        MediaType.IMAGE_JPEG_VALUE,
        new byte[]{100});
    zip = new MockMultipartFile("game.zip",
        "original_game_file.zip",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        new byte[]{100});
  }

  @Test
  @DisplayName("이미지 저장 성공")
  void uploadFile_image(){
    // Given
    when(s3.putObject(any())).thenReturn(null);
    // When
    FileDetail fileDetail = s3Service.uploadFile(image);
    // Then
    assertThat(fileDetail.getFileType()).isEqualTo(MediaType.IMAGE_JPEG_VALUE);
    assertThat(fileDetail.getBucket()).isEqualTo(imageBucket);
  }

  @Test
  @DisplayName("압축 파일 저장 성공")
  void uploadFile_zip(){
    // Given
    when(s3.putObject(any())).thenReturn(null);
    // When
    FileDetail fileDetail = s3Service.uploadFile(zip);
    // Then
    assertThat(fileDetail.getFileType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    assertThat(fileDetail.getBucket()).isEqualTo(gameBucket);
  }

  @Test
  @DisplayName("파일 삭제 성공")
  void deleteFile(){
    // Given
    FileDetail fileDetail = new FileDetail();
    FileDetail savedFileDetail = fileDetailRepository.save(fileDetail);
    // When
    fileDetailRepository.delete(savedFileDetail);
    // Then
    assertThrows(NotFoundException.class,() -> fileDetailRepository.findByFilePathOrElseThrow(savedFileDetail.getFilePath()));
  }

  @Test
  @DisplayName("다중 파일 업로드 성공")
  void uploadFiles_Success() throws ExecutionException, InterruptedException {
    // Given
    List<MultipartFile> files = List.of(image, image);

    // When
    CompletableFuture<List<FileDetail>> resultFuture = s3Service.uploadFiles(files);
    List<FileDetail> fileDetails = resultFuture.get();

    // Then
    assertThat(fileDetails).hasSize(2);
    assertThat(fileDetails.get(0).getFilePath()).contains("s3.ap-northeast-2.amazonaws.com");
    List<FileDetail> savedFiles = fileDetailRepository.findAll();
    assertThat(savedFiles).hasSize(2);
  }
}
