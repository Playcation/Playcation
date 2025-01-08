package com.example.playcation.s3.controller;

import com.example.playcation.s3.service.S3Service;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class S3Controller {

  private final S3Service s3Service;

  @PostMapping
  public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
    String url = s3Service.uploadFile(file);
    return ResponseEntity.ok(url);
  }

  @PostMapping("/files")
  public ResponseEntity<List<String>> uploadFiles(
      @RequestPart(value = "files") List<MultipartFile> files
  ){
    CompletableFuture<List<String>> urls = s3Service.uploadFiles(files);
    return ResponseEntity.ok(urls.join());
  }

  @GetMapping(value = "/download", produces={MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
  public ResponseEntity<byte[]> downloadFile(
      @RequestParam String filePath
  ) throws IOException {
    return ResponseEntity.ok().body(s3Service.getObject(filePath));
  }
}
