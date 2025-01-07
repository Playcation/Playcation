package com.example.playcation.s3.controller;

import com.example.playcation.s3.service.S3Service;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
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
      @RequestPart("files") List<MultipartFile> files
  ){
    List<String> urls = s3Service.uploadFiles(files);
    return ResponseEntity.ok(urls);
  }

}
