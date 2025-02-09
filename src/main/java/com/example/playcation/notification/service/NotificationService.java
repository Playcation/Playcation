package com.example.playcation.notification.service;

import com.example.playcation.game.entity.Game;
import com.example.playcation.redis.RedisPublisher;
import com.example.playcation.review.entity.Review;
import com.example.playcation.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final ReviewRepository reviewRepository;
  private final RedisPublisher redisPublisher;
  private final SseEmitterRegistry sseEmitterRegistry;


  /**
   * SSE(Server-Sent Events) 구독 메서드
   * 클라이언트가 SSE를 통해 서버로부터 이벤트를 받을 수 있도록 SseEmitter를 생성하고 관리
   *
   * @param userId SSE를 구독할 사용자 ID
   * @return 생성된 SseEmitter 객체
   */
  public SseEmitter subscribe(Long userId) {
    // 기존 SSE 연결이 있다면 먼저 제거
    List<SseEmitter> existingEmitters = sseEmitterRegistry.getEmitters(userId);
    for (SseEmitter existingEmitter : existingEmitters) {
      sseEmitterRegistry.removeEmitter(userId, existingEmitter); // 기존 sse 연결있으면 제거!!!!!(안하면 오류남)
    }

    // 1. 클라이언트와의 SSE 연결을 위한 SseEmitter 객체 생성
    // Long.MAX_VALUE를 설정하여 타임아웃 없이 최대한 오랫동안 연결을 유지
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

    // 2. 클라이언트에 "connect" 이벤트를 전송하여 연결이 성공적으로 된것을 나타냄
    try {
      sseEmitter.send(SseEmitter.event().name("connect"));
    } catch (IOException e) {
      // 연결 시 오류가 발생하면 콘솔에 스택 트레이스 출력
      e.printStackTrace(); // 보안상 좋지않아서 logging으로 변경할지 고민중
      sseEmitter.completeWithError(e);
      return sseEmitter;
    }

    // 3. NotificationController에서 관리하는 전역 SSE 연결 Map에 현재 사용자 ID와 SseEmitter를 저장
    // SseEmitterRegistry에 등록
    sseEmitterRegistry.addEmitter(userId, sseEmitter);
    System.out.println("SSE 연결 성공! userId : " + userId);

    // 4. 연결 종료 처리: 클라이언트 연결이 완료되거나 타임아웃 또는 오류가 발생할 때 해당 사용자의 SseEmitter를 제거
    sseEmitter.onCompletion(() -> sseEmitterRegistry.removeEmitter(userId, sseEmitter));
    sseEmitter.onTimeout(() -> sseEmitterRegistry.removeEmitter(userId, sseEmitter));
    sseEmitter.onError((e) -> sseEmitterRegistry.removeEmitter(userId, sseEmitter));

    // 5. 생성된 sseEmitter 객체 반환
    return sseEmitter;
  }
  /**
   * 리뷰 알림 전송 메서드
   * 리뷰가 생성되면 해당 리뷰와 연결된 게임의 등록자(user)에게 알림 메시지를 Redis를 통해 발행
   * @param reviewId 알림을 발송할 리뷰의 ID
   */
  @Transactional
  public void notifyReview(Long reviewId) {

    // 1. 리뷰 조회
    Review review = reviewRepository.findByIdOrElseThrow(reviewId);

    // 2. 조회한 리뷰에서 연결된 게임(Game) 객체를 가져옴
    Game game = review.getGame();
    if (game == null) {
      throw new IllegalStateException("리뷰와 연관된 게임이 없습니다.");
    }

    // 3. 게임 등록자(User)의 ID를 가져옵니다.
    Long userId = game.getUser().getId();
    String message = userId + " : " + game.getTitle() + "에 새로운 리뷰가 작성되었습니다.";

    // Redis 예외가 발생해도 리뷰 생성이 정상적으로 진행되도록 try-catch 추가
    try {
      redisPublisher.publish(message);
      System.out.println("리뷰 알림 전송, 메시지: " + message);
    } catch (Exception e) {
      // 예외가 발생해도 트랜잭션에 영향을 주지 않도록 하고, 예외 메세지 출력
      System.out.println("Redis 알림 전송 실패: " + e.getMessage());
    }

  }
}
