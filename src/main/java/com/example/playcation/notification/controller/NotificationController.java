package com.example.playcation.notification.controller;

import com.example.playcation.notification.service.NotificationService;
import com.example.playcation.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * "/games/{gameId}/reviews"
 * gameId : 리뷰가 달릴 게임 id
 */
@RequiredArgsConstructor
@RestController
public class NotificationController {

  private final NotificationService notificationService;
  private final JWTUtil jwtUtil;

  /**
   * SSE 구독(연결) 엔드포인트
   * 클라이언트가 이 엔드포인트에 GET 요청을 보내면,
   * JWT 토큰에서 사용자 ID를 추출한 후 NotificationService를 통해 SSE 연결(SseEmitter)을 생성하여 반환한다.
   * MIME 타입은 text/event-stream으로 설정되어 있어, 클라이언트가 SSE 연결을 인식할 수 있도록 한다.
   * @param token 요청 헤더에 포함된 JWT 액세스 토큰 (TokenSettings.ACCESS_TOKEN_CATEGORY 이름으로 전달)
   * @return 생성된 SseEmitter 객체 (실시간 이벤트 스트림)
   */
  @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(@RequestParam ("token") String token) {
    Long userId = jwtUtil.findUserByToken(token);

    // NotificationService의 subscribe 메서드를 호출하여 해당 사용자에 대한 SSE 연결을 생성 및 반환합니다.
    return notificationService.subscribe(userId);
  }
}
