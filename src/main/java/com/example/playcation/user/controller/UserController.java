package com.example.playcation.user.controller;

import com.example.playcation.user.dto.LoginUserRequestDto;
import com.example.playcation.user.dto.LoginUserResponseDto;
import com.example.playcation.user.dto.DeletedUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UpdatedUserPasswordRequestDto;
import com.example.playcation.user.dto.UpdatedUserRequestDto;
import com.example.playcation.user.service.UserService;
import com.example.playcation.util.JwtTokenProvider;
import com.example.playcation.util.TokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final TokenUtil tokenUtil;

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<LoginUserResponseDto> login(
      @Valid @RequestBody LoginUserRequestDto userLoginRequestDto
  ) {
    LoginUserResponseDto responseDto = userService.login(userLoginRequestDto);
    return ResponseEntity.ok(responseDto);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(
      @RequestHeader("Authorization") String authorizationHeader
  ){

    return ResponseEntity.ok().body("로그아웃 되었습니다.");
  }

  @PostMapping("/sign-in")
  public ResponseEntity<UserResponseDto> signUp(
      @Valid @RequestBody SignInUserRequestDto userSignInRequestDto
  ) {
    return ResponseEntity.ok().body(userService.signUp(userSignInRequestDto));
  }

  @GetMapping
  public ResponseEntity<UserResponseDto> findUser(
      @RequestHeader("Authorization") String authorizationHeader
  ){
    Long id = tokenUtil.findUserByToken(authorizationHeader);

    return ResponseEntity.ok().body(userService.findUser(id));
  }

  @PutMapping
  public ResponseEntity<UserResponseDto> updateUser(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestBody UpdatedUserRequestDto userUpdateRequestDto
  ){
    Long id = tokenUtil.findUserByToken(authorizationHeader);

    return ResponseEntity.ok().body(userService.updateUser(id, userUpdateRequestDto));
  }

  @PatchMapping("/password")
  public ResponseEntity<UserResponseDto> changePassword(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestBody UpdatedUserPasswordRequestDto userUpdatePasswordRequestDto
  ){
    Long id = tokenUtil.findUserByToken(authorizationHeader);
    return ResponseEntity.ok().body(userService.updateUserPassword(id, userUpdatePasswordRequestDto));
  }

  //회원 탈퇴
  @DeleteMapping("/delete")
  public String deleteAccount(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestBody DeletedUserRequestDto userLogoutRequestDto
  ) {
    Long id = tokenUtil.findUserByToken(authorizationHeader);
    // 탈퇴 처리 메서드 호출
    userService.delete(id, userLogoutRequestDto);
    return "회원 탈퇴가 완료되었습니다.";
  }
}
