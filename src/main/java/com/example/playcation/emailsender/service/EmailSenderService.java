package com.example.playcation.emailsender.service;

import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

  private final JavaMailSender javaMailSender; // 실제 이메일을 보내는 역할

  @Value("${spring.mail.username}") // application.properties에 있는 메일 설정 값을 가져옴
  private String senderEmail; // 이메일을 보낼 때 사용하는 발신자 이메일 주소

  /**
   * 주문 완료 이메일 전송
   * @param order 주문 정보
   * @param orderDetails 주문 상세 정보
   */
  public void sendOrderConfirmationEmail(Order order, List<OrderDetail> orderDetails)
      throws MessagingException {
    // MimeMessage 객체 생성
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

    // MimeMessageHelper를 사용하여 MimeMessage 구성
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

    String recipientEmail = order.getUser().getEmail(); // 수신자 이메일 설정
    mimeMessageHelper.setTo(recipientEmail); // 수신자 이메일 설정
    mimeMessageHelper.setFrom(senderEmail); // 발신자 이메일 설정
    mimeMessageHelper.setSubject("[주문 결제 완료]"); // 이메일 제목 설정
    mimeMessage.setContent(buildEmailContent(order, orderDetails), "text/html; charset=UTF-8"); // HTML 본문 설정

    javaMailSender.send(mimeMessage); // 이메일 전송
  }


  /**
   * 이메일 내용 생성
   * @param order 주문 정보
   * @param orderDetails 주문 상세 정보
   * @return HTML 이메일 내용
   */
  private String buildEmailContent(Order order, List<OrderDetail> orderDetails) {
    StringBuilder details = new StringBuilder(); // 주문 내역을 작성하기 위해 StringBuilder 사용
    BigDecimal totalAmount = BigDecimal.ZERO; // 총 금액을 계산할 변수, 초기값은 0

    // 주문 내역을 HTML 테이블 형식으로 작성
    details.append("<table style='width:100%; border-collapse: collapse;'>");
    details.append("<tr><th style='border: 1px solid #ddd; padding: 8px;'>게임 제목</th><th style='border: 1px solid #ddd; padding: 8px;'>가격</th></tr>");

    // 주문 내역에 대해 순차적으로 처리
    for (OrderDetail detail : orderDetails) {
      BigDecimal price = detail.getPrice();  // 주문 상세에서 할인된 가격을 가져옴
      details.append("<tr><td style='border: 1px solid #ddd; padding: 8px;'>")
          .append(detail.getGame().getTitle())
          .append("</td><td style='border: 1px solid #ddd; padding: 8px;'>")
          .append(price.toPlainString())
          .append("원</td></tr>");
      totalAmount = totalAmount.add(price); // 총 금액에 해당 가격을 더함
      totalAmount = totalAmount.setScale(0, RoundingMode.FLOOR); // 소수점 아래를 버리고 정수만 가져오기
    }

    details.append("</table><br>"); // 테이블 종료

    // 이메일 본문
    return String.format(
        "<div style='font-family: Arial, sans-serif;'>" +
            "<h2>안녕하세요, %s님.</h2>" +
            "<p>주문이 완료되었습니다.</p>" +
            "<p><strong>총 금액:</strong> %s원</p>" +
            "<p><strong>주문 시간:</strong> %s</p>" +
            "<h3>주문 내역:</h3>" +
            "%s" + // 주문 내역 테이블
            "<p>이용해 주셔서 감사합니다.</p>" +
            "</div>",
        order.getUser().getName(), // 주문자의 이름
        totalAmount,  // 총 금액
        order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), // 주문 시간
        details.toString() // HTML 형식의 주문 내역
    );
  }
}