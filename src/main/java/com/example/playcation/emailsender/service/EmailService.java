package com.example.playcation.emailsender.service;

import com.example.playcation.redis.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final RedisUtil redisUtil;

  // EmailService 클래스에서 로그를 관리하는 Logger 생성(예외 메세지, 예외 객체 로그로 출력하기 위함)
  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  private int authNumber; // 인증코드

  public EmailService(JavaMailSender mailSender, RedisUtil redisUtil) {
    this.mailSender = mailSender;
    this.redisUtil = redisUtil;
  }

  /**
   * 입력된 이메일(email)과 인증번호(authNum)를 검증하는 메서드
   * @param email 사용자가 입력한 이메일
   * @param authNum 사용자가 입력한 인증코드
   * @return 이메일과 인증번호가 일치하면 true, 그렇지 않으면 false(Null인 경우도)
   */
  public boolean CheckAuthNum(String email, String authNum){
    if (redisUtil.getData(authNum) == null){
      return false;
    } else if (redisUtil.getData(authNum).equals(email)){
      // Redis에 저장된 이메일과 입력된 이메일이 일치하면 true 반환
      return true;
    } else {
      // 인증번호는 존재하지만 이메일이 일치하지 않으면 false 반환
      return false;
    }
  }

  // 임의의 6자리 정수생성 메서드
  public void makeRandomNumber() {
    Random r = new Random(); // 랜덤 객체 생성
    String randomNumber = ""; // 랜덤 숫자 저장할 String 변수 초기화

    // 6번 반복해서 한 자리 숫자 랜덤으로 생성
    for(int i = 0; i < 6; i++) {
      // 0~9 사이의 숫자 생성후 문자열로 변환 randomNumber에 추가
      randomNumber += Integer.toString(r.nextInt(10));
    }
    // 생성된 문자열을 정수로 변환해서 저장(첫 숫자가 0으로 시작하는 경우, 문제없이 이어 붙히기 위해서)
    authNumber = Integer.parseInt(randomNumber);
  }


  // 메일 본문 설정
  public String joinEmail(String email) {
    makeRandomNumber(); // 인증번호 랜덤 생성
    String setFrom = "cationplay@gmail.com"; // email-config에 설정한 발신자 이메일 주소를 입력
    String toMail = email; // 수신자
    String title = "[Playcation] 회원가입 인증 메일"; // 이메일 제목
    String content =
        "<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\"> "
            + "<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">Playcation</span><br/>\n"
            + "<span style=\"color: #0c1a95;\">임시 비밀번호</span> 안내입니다.</h1>" +
        "<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">\n"
            + " 안녕하세요. Playcation입니다.<br/>\n"
            + " 요청하신 <strong><span style=\"font-size: 17px; color: #0c1a95;\">이메일 인증코드</span></strong>가\n"
            + " 생성되었습니다.<br/>\n"
            + " 해당 인증코드를 입력해주세요.<br/>\n"
            + " <br/>"
            + authNumber // 인증코드
            + " </p>";

    mailSend(setFrom, toMail, title, content); // 발신자, 수신자, 제목
    return Integer.toString(authNumber); // 인증코드(정수 6자리)
  }

  //이메일을 전송합니다.
  public void mailSend(String setFrom, String toMail, String title, String content) {
    MimeMessage message = mailSender.createMimeMessage(); //JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8"); //이메일 메시지와 관련된 설정을 수행한다.
      // true를 전달하여 multipart(html,첨부파일,이미지 등) 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
      helper.setFrom(setFrom);// 이메일의 발신자 주소 설정
      helper.setTo(toMail);   // 이메일의 수신자 주소 설정
      helper.setSubject(title); // 이메일의 제목을 설정
      helper.setText(content,true); // 이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
      mailSender.send(message);
    } catch (MessagingException e) {
      // 이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
      // ex) cationplay, @naver.com ...
      // 이러한 경우 MessagingException이 발생
      logger.error("예외 발생", e); // 예외 메세지랑 예외 객체를 로그로 출력(printStackTrace()는 보안상 사용x)
    }
    // 인증번호를 String 타입으로 변환하여 Redis 키로 사용
    // Redis에 인증번호(authNumber)를 키로, 이메일(toMail)을 값으로 저장하고, 5분(60*5L) 동안 유효하도록 설정
    redisUtil.setDataExpire(Integer.toString(authNumber),toMail,60*5L);
  }
}
