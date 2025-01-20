package com.example.playcation.emailsender.service;

import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
  public void sendOrderConfirmationEmail(Order order, List<OrderDetail> orderDetails) {
    SimpleMailMessage message = new SimpleMailMessage();

    String recipientEmail = order.getUser().getEmail(); // 주문자 이메일
    message.setFrom(senderEmail); // 발신자 이메일 설정
    message.setTo(recipientEmail); // 수신자 이메일 설정
    message.setSubject("주문 완료 알림"); // 이메일 제목 설정
    message.setText(buildEmailContent(order, orderDetails)); // 본문 buildEmailContent 메서드를 통해 생성,주문정보, 주문상세 정보(할인된것 포함)

    javaMailSender.send(message); // 이메일 전송
  }

  /**
   * 이메일 내용 생성
   * @param order 주문 정보
   * @param orderDetails 주문 상세 정보
   * @return 이메일 내용
   */
  private String buildEmailContent(Order order, List<OrderDetail> orderDetails) {
    StringBuilder details = new StringBuilder(); // 이메일 본문에 주문 내역을 작성하기 위해 StringBuilder를 사용
    BigDecimal totalAmount = BigDecimal.ZERO; // 총 금액을 계산할 변수, 초기값은 0, order_detail entity에서 price는 BigDecimal

    // 주문 내역에 대해 순차적으로 처리
    for (OrderDetail detail : orderDetails) {
      BigDecimal price = detail.getPrice();  // 주문 상세에서 할인된 가격을 가져옴
      // 게임 제목과 가격을 이메일 본문에 추가
      details.append(String.format("- %s: %s원\n", detail.getGame().getTitle(), price.toPlainString()));
      totalAmount = totalAmount.add(price); // 총 금액에 해당 가격을 더함
      // 소수점 아래를 버리고 정수만 가져오기
      totalAmount = totalAmount.setScale(0, RoundingMode.FLOOR);
    }

    // 이메일 본문
    // String.format :
    return String.format(
        "안녕하세요, [%s]님.\n\n" +
            "주문이 완료되었습니다.\n\n" +
            "총 금액: %s원\n" +
            "주문 시간: %s\n\n" +
            "주문 내역:\n%s\n\n" +
            "이용해 주셔서 감사합니다.",
        order.getUser().getName(), // 주문자의 이름 출력
        totalAmount,  // 총 금액을 출력
        order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), // 주문 시간에 초까지 포함
        details.toString() // 주문 내역을 이메일 본문에 표시하기 위한 역할
    );
  }
}