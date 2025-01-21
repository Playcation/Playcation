package com.example.playcation.emailsender.controller;


import com.example.playcation.emailsender.dto.EmailAuthResponseDto;
import com.example.playcation.emailsender.entity.EmailMessage;
import com.example.playcation.emailsender.service.EmailAuthService;
import com.example.playcation.user.dto.LoginUserRequestDto;
import com.example.playcation.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class EmailAuthController {

  private final EmailAuthService emailAuthService;

  // 이메일 인증 코드 전송
  @PostMapping("/email")
  public ResponseEntity<EmailAuthResponseDto> sendJoinMail(@RequestBody LoginUserRequestDto loginUserRequestDto) {
    // 이메일 메시지에서 수신자 정보를 가져옴
    String to = loginUserRequestDto.getEmail();
    String subject = "[Playcation] 이메일 인증을 위한 인증 코드 발송"; // 이메일 제목

    // 이메일 보내고 인증번호 받기
    String authCode = emailAuthService.sendMail(to, subject);

    // 인증번호 반환을 위한 DTO 설정 (생성자를 통해 초기화)
    EmailAuthResponseDto emailAuthResponseDto = new EmailAuthResponseDto(authCode);

    // ResponseEntity로 응답 반환
    return ResponseEntity.ok(emailAuthResponseDto);
  }
}