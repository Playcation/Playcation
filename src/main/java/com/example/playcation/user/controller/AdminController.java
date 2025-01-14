package com.example.playcation.user.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.Role;
import com.example.playcation.tag.Dto.CreatedTagRequestDto;
import com.example.playcation.tag.Dto.CreatedTagResponseDto;
import com.example.playcation.tag.service.TagService;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.service.AdminService;
import com.example.playcation.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;
  private final UserService userService;
  private final TagService tagService;

  // admin 생성
  @PostMapping("/sign-in")
  public ResponseEntity<UserResponseDto> signUp() {
    return ResponseEntity.ok().body(adminService.signUp());
  }

  // 태그 생성
  @PostMapping("/tags")
  public ResponseEntity<CreatedTagResponseDto> createTag(@RequestBody CreatedTagRequestDto requestDto) {
    CreatedTagResponseDto responseDto = tagService.CreateTag(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  // 모든 유저 조회
  @GetMapping("/users")
  public ResponseEntity<PagingDto<UserResponseDto>> findAllUsers(
      @PageableDefault(size = 10, page = 0, sort = "role") Pageable pageable
  ){
    return ResponseEntity.ok().body(userService.findAllUsers(pageable));
  }

  // ADMIN
  @PutMapping("/users/{userId}/update/role")
  public ResponseEntity<UserResponseDto> updateUserRole(
      @PathVariable() Long userId
  ){
    return ResponseEntity.ok().body(adminService.updateUserAuth(userId));
  }
}
