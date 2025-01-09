package com.example.playcation.s3.controller;

import com.example.playcation.s3.dto.FileResponseDto;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.service.S3Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class S3Controller {

  private final S3Service s3Service;

  @PostMapping
  public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
    FileDetail fileDetail = s3Service.uploadFile(file);
    return ResponseEntity.ok(fileDetail.getFilePath());
  }

  @PostMapping("/files")
  public ResponseEntity<List<String>> uploadFiles(
      @RequestPart(value = "files") List<MultipartFile> files
  ){
    CompletableFuture<List<String>> urls = s3Service.uploadFiles(files);
    return ResponseEntity.ok(urls.join());
  }

  @GetMapping(value = "/games/{gameId}/download/zip", produces="application/octet-stream")
  public ResponseEntity<byte[]> downloadGameZip(
      @PathVariable Long gameId
  ) throws IOException {
    FileResponseDto fileDTO = s3Service.getObjectByGameId(gameId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentLength(fileDTO.getFile().length);

//    ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
//        .filename(fileDTO.getFileName(), StandardCharsets.UTF_8)
//        .build();
//    headers.setContentDisposition(contentDisposition);

    return ResponseEntity.ok().headers(headers).body(fileDTO.getFile());
  }
}
