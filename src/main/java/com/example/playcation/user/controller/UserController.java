package com.example.playcation.user.controller;

import com.example.playcation.user.dto.UserLoginRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.service.UserService;
import com.example.playcation.util.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<UserResponseDto> login(
      @Valid @RequestBody UserLoginRequestDto userLoginRequestDto
  ) {
    UserResponseDto responseDto = userService.login(
        userLoginRequestDto.getEmail(),
        userLoginRequestDto.getPassword()
    );
    return ResponseEntity.ok(responseDto);
  }

  //회원 탈퇴
  @DeleteMapping("/delete")
  public String deleteAccount(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam String password
  ) {
    // Authorization 헤더에서 JWT 토큰 추출
    String token = authorizationHeader.replace("Bearer ", "").trim();
    String email = jwtTokenProvider.getUsername(token);
    // 탈퇴 처리 메서드 호출
    userService.delete(token, password);
    return "회원 탈퇴가 완료되었습니다.";
  }
}
