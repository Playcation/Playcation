package com.example.playcation.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private int mailPort;

  @Value("${spring.mail.username}")
  private String mailUsername;

  @Value("${spring.mail.password}")
  private String mailPassword;

  @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
  private int connectionTimeout;

  @Value("${spring.mail.properties.mail.smtp.timeout}")
  private int timeout;

  @Value("${spring.mail.properties.mail.smtp.writetimeout}")
  private int writeTimeout;

  /**
   * JavaMailSender : 이메일 전송을 위한 빈 설정 코드
   * mailSender : 이메일을 설정 할 때 사용
   */

  @Bean
  public JavaMailSender mailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(mailHost); // 이메일 서버의 호스트 주소 설정(해당 서버 주소) SMTP 서버 주소(ex. smtp.gamil.com) 설정
    mailSender.setPort(mailPort); // 포트 번호를 담고 있는 변수
    mailSender.setUsername(mailUsername); // 이메일 서버 접속할 때 사용할 비밀번호 설정(사용자 이름을 담고 있는 변수)
    mailSender.setPassword(mailPassword); // 이메일 서버 접속할 때 사용할 비밀번호 설정(사용자 비밀번호를 담고 있는 변수)
    mailSender.setDefaultEncoding("UTF-8");
    mailSender.setJavaMailProperties(getMailProperties());

    return mailSender;
  }

    /**
     * getJavaMailProperties()는 이메일 서버의 설정을 저장할 Properties 객체를 반환
     * 이 Properties 객체를 사용하여 추가적인 SMTP 설정을 할 수 있음
     */
  private Properties getMailProperties() {
    Properties props = new Properties();

    // mail.transport.protocol은 이메일 전송에 사용할 프로토콜을 설정
    // SMTP(Simple Mail Transfer Protocol)를 사용
    props.put("mail.transport.protocol", "smtp");

    // mail.smtp.auth는 SMTP 인증을 활성화, 이메일 서버가 로그인 인증을 요구할 때 설정해야 함
    // true로 설정하면 인증을 요구함, 사용자 이름과 비밀번호가 필요함을 의미
    props.put("mail.smtp.auth", "true");

    // mail.smtp.starttls.enable은 TLS(Transport Layer Security)를 사용하여 이메일 전송을 암호화할지 말지 설정
    // true로 설정하면 TLS 암호화가 활성화되며, 서버와의 연결을 보안 상태로 유지함
    props.put("mail.smtp.starttls.enable", "true");

    // mail.debug는 이메일 전송 과정에서 디버깅 정보를 출력할지 말지 정하는 것
    // true로 설정하면 이메일 전송 로그가 콘솔에 출력되어 디버깅, 운영 환경에서는 보안 문제로 false
    props.put("mail.debug", "true");

    props.put("mail.smtp.starttls.required", "ture");
    props.put("mail.smtp.connectiontimeout", connectionTimeout);
    props.put("mail.smtp.timeout", timeout);
    props.put("mail.smtp.writetimeout", writeTimeout);


    // 설정을 마친 mailSender 객체를 반환
    // 이메일을 전송하는 데 사용할 수 있는 JavaMailSender 빈으로 Spring IoC 컨테이너에 등록됨
    return props;
  }
}