package com.example.playcation.emailsender.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
public class EmailMessage {

  private String email;      // 이메일 수신자

}
