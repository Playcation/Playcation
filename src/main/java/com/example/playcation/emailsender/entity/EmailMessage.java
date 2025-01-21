package com.example.playcation.emailsender.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
public class EmailMessage {

  private String to;      // 이메일 수신자
  private String subject; // 이메일 제목
  private String text;    // 이메일 본문

  // Builder 패턴을 사용하여 객체 생성
  public EmailMessage(String to, String subject, String text) {
    this.to = to;
    this.subject = subject;
    this.text = text;
  }
}
