package com.example.playcation.redis;

import com.example.playcation.notification.service.SseEmitterRegistry;
import java.util.List;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * RedisSubscriber는 Redis Pub/Sub 채널에서 발행된 메시지를 구독(Listen)해서
 * 처리(예: SSE를 통해 클라이언트에게 알림 전송)하는 역할을 한다.
 */
@Component
public class RedisSubscriber implements MessageListener {

  private final SseEmitterRegistry sseEmitterRegistry;

  public RedisSubscriber(SseEmitterRegistry sseEmitterRegistry) {
    this.sseEmitterRegistry = sseEmitterRegistry;
  }

  /**
   * Redis에서 메시지가 수신될 때마다 호출되는 메서드
   * @param message Redis로부터 수신한 메시지 객체 (메시지 본문은 byte[] 형태로 제공됨)
   * @param pattern Redis 패턴 (사용하지 않을 경우 null일 수 있음)
   */

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String payload = new String(message.getBody());
    System.out.println("Redis에서 받은 메시지: " + payload);

    Long userId = extractUserIdFromMessage(payload);
    String notificationMessage = extractNotificationMessage(payload);



    // 해당 사용자의 모든 SSE Emitter에 메시지 전송
    List<SseEmitter> emitters = sseEmitterRegistry.getEmitters(userId);
    for (SseEmitter emitter : emitters) {
      try {
        System.out.println(" SSE 전송 시작: " + notificationMessage);
        emitter.send(SseEmitter.event().name("newReview").data(notificationMessage));
        System.out.println(" SSE 전송 완료: " + notificationMessage);
      } catch (Exception e) {
        System.err.println(" SSE 전송 실패: " + e.getMessage());
        sseEmitterRegistry.removeEmitter(userId, emitter);
      }
    }
  }


  /**
   * 메시지에서 사용자 ID를 추출하는 헬퍼 메서드
   * @param payload Redis로부터 수신한 메시지 본문 (예: "123:게임 제목에 새로운 리뷰가 작성되었습니다.")
   * @return 추출된 사용자 ID
   */
  private Long extractUserIdFromMessage(String payload) {
    // ":" 기준으로 메시지를 분리한 후, 첫 번째 부분의 공백을 제거하고 Long 타입으로 변환하여 반환
    String[] parts = payload.split(":");
    return Long.valueOf(parts[0].trim());
  }

  /**
   * 메시지에서 실제 알림 메시지를 추출하는 헬퍼 메서드입니다.
   * @param payload Redis로부터 수신한 메시지 본문 (예: "123 : [게임 제목]에 새로운 리뷰가 작성되었습니다.")
   * @return 추출된 알림 메시지
   */
  private String extractNotificationMessage(String payload) {
    String[] parts = payload.split(":");
    if (parts.length > 1) {
      return parts[1].trim(); // 공백 제거
    }
    return payload; // 예외 처리
  }
}