package com.example.playcation.emailsender.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
  private final JavaMailSender javaMailSender;

  private final String templatePath = "resources/MailAuth.html";  // resources/templates/ 내의 HTML 파일 경로

  public String sendMail(String to, String subject) {
    // 인증번호 생성
    String authNum = createCode();

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(to); // 메일 수신자
      mimeMessageHelper.setSubject(subject); // 메일 제목
      // HTML 템플릿을 읽고 인증번호를 삽입
      String emailContent = readHtmlTemplateAndReplaceCode(authNum);

      mimeMessageHelper.setText(emailContent, true); // HTML 본문 내용, HTML 여부
      javaMailSender.send(mimeMessage);

      return authNum;

    } catch (MessagingException | IOException e) {
      throw new RuntimeException("이메일 전송에 실패하였습니다.", e);
    }
  }

  // 인증번호 생성 메서드
  public String createCode() {
    Random random = new Random();
    StringBuffer key = new StringBuffer();

    for (int i = 0; i < 8; i++) {
      int index = random.nextInt(4);

      switch (index) {
        case 0: key.append((char) ((int) random.nextInt(26) + 97)); break;
        case 1: key.append((char) ((int) random.nextInt(26) + 65)); break;
        default: key.append(random.nextInt(9));
      }
    }
    return key.toString();
  }

  // HTML 템플릿을 읽고, 인증번호를 삽입하는 메서드
  private String readHtmlTemplateAndReplaceCode(String authNum) throws IOException {
    // 리소스에서 HTML 파일을 읽어옵니다.
    ClassPathResource resource = new ClassPathResource(templatePath);
    String emailTemplate = new String(Files.readAllBytes(resource.getFile().toPath()), "UTF-8");

    // 인증번호를 삽입합니다.
    return emailTemplate.replace("{{authNum}}", authNum);  // {{authNum}} 자리 표시자를 인증번호로 교체
  }
}
