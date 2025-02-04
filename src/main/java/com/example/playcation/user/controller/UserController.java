package com.example.playcation.user.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.user.dto.DeletedUserRequestDto;
import com.example.playcation.user.dto.RegistManagerRequestDto;
import com.example.playcation.user.dto.RestoreUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UpdatedUserPasswordRequestDto;
import com.example.playcation.user.dto.UpdatedUserRequestDto;
import com.example.playcation.user.service.UserService;
import com.example.playcation.util.JWTUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final JWTUtil jwtUtil;

  // 회원 가입
  @PostMapping("/sign-in")
  public ResponseEntity<UserResponseDto> signUp(
      @Valid @RequestPart(value = "json") SignInUserRequestDto userSignInRequestDto,
      @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    return ResponseEntity.ok().body(userService.signUp(userSignInRequestDto, file));
  }

  // 유저 프로필 조회
  @GetMapping
  public ResponseEntity<UserResponseDto> findUser(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ){
    Long id = jwtUtil.findUserByToken(authorizationHeader);

    return ResponseEntity.ok().body(userService.findUser(id));
  }

  // 유저 프로필 조회
  @GetMapping("/search")
  public ResponseEntity<PagingDto<UserResponseDto>> searchUser(
      @RequestParam String username,
      @PageableDefault(size = 10, page = 0, sort = "username") Pageable pageable
  ){
    return ResponseEntity.ok().body(userService.searchUser(username, pageable));
  }

  // 유저 수정
  @PutMapping
  public ResponseEntity<UserResponseDto> updateUser(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @Valid @RequestPart(value = "json") UpdatedUserRequestDto userUpdateRequestDto,
      @RequestPart(required = false) MultipartFile file
  ){
    Long id = jwtUtil.findUserByToken(authorizationHeader);

    return ResponseEntity.ok().body(userService.updateUser(id, userUpdateRequestDto, file));
  }

  // 비밀번호 변경
  @PatchMapping("/password")
  public ResponseEntity<UserResponseDto> changePassword(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @Valid @RequestBody UpdatedUserPasswordRequestDto userUpdatePasswordRequestDto
  ){
    Long id = jwtUtil.findUserByToken(authorizationHeader);
    return ResponseEntity.ok().body(userService.updateUserPassword(id, userUpdatePasswordRequestDto));
  }

  //회원 탈퇴
  @DeleteMapping("/delete")
  public String deleteAccount(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @Valid @RequestBody DeletedUserRequestDto userLogoutRequestDto
  ) {
    Long id = jwtUtil.findUserByToken(authorizationHeader);
    // 탈퇴 처리 메서드 호출
    userService.delete(id, userLogoutRequestDto);
    return "회원 탈퇴가 완료되었습니다.";
  }

  // 회원 복구
  @PutMapping("/restore")
  public ResponseEntity<UserResponseDto> restoreUser(
      @Valid @RequestBody RestoreUserRequestDto requestDto
  ){
    return ResponseEntity.ok().body(userService.restoreUser(requestDto));
  }

  // 출석 시
  @PutMapping("/attendance")
  public ResponseEntity<String> attendanceUser(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ){
    Long id = jwtUtil.findUserByToken(authorizationHeader);
    return ResponseEntity.ok().body(userService.attendanceUser(id));
  }

  @PostMapping("/manager")
  public ResponseEntity<String> registerManager(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestPart RegistManagerRequestDto registManagerRequestDto,
      @RequestPart MultipartFile gameImage
  ){
    Long id = jwtUtil.findUserByToken(authorizationHeader);
    String str = userService.registerManager(id, registManagerRequestDto, gameImage);
    return ResponseEntity.ok().body(str);
  }

  @PostMapping("/upload/files")
  public ResponseEntity<UserResponseDto> uploadFiles(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestPart(value = "files") List<MultipartFile> files
  ){
    Long id = jwtUtil.findUserByToken(authorizationHeader);
    return ResponseEntity.ok().body(userService.uploadFiles(id, files));
  }
}
