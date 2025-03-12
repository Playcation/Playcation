package com.example.playcation.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api")
public class TestController {

  @GetMapping("/test")
  public ResponseEntity<String> testEndpoint() {
    return ResponseEntity.ok("JWT Filter Test Passed!");
  }
}
