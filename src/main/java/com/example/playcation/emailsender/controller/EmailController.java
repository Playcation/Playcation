package com.example.playcation.emailsender.controller;

import com.example.playcation.emailsender.dto.EmailCheckDto;
import com.example.playcation.emailsender.dto.EmailRequestDto;
import com.example.playcation.emailsender.service.EmailService;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.UserErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

  private final EmailService mailService;

  @PostMapping("/email")
  public String mailSend(@RequestBody @Valid EmailRequestDto emailDto) {
    System.out.println("이메일 인증 요청이 들어옴");
    System.out.println("이메일 인증 이메일 :" + emailDto.getEmail());
    return mailService.joinEmail(emailDto.getEmail());
  }

  @PostMapping("/mail-check")
  public String AuthCheck(@RequestBody @Valid EmailCheckDto emailCheckDto) {
    Boolean Checked = mailService.CheckAuthNum(emailCheckDto.getEmail(),
        emailCheckDto.getAuthNum());
    if (Checked) { // 요청 이메일, 인증코드
      return "이메일 인증이 완료되었습니다.";
    } else {
      throw new NotFoundException(UserErrorCode.AUTH_NOT_EMAIL);
    }
  }
}
