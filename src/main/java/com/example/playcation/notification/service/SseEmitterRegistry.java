package com.example.playcation.notification.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterRegistry {

  // 사용자 ID 별로 여러 Emitter를 저장할 수 있도록 함.
  private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

  /**
   * 사용자 ID에 해당하는 SSE Emitter 추가
   */
  public void addEmitter(Long userId, SseEmitter emitter) {
    List<SseEmitter> userEmitters = emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
    userEmitters.add(emitter);
  }

  /**
   * 사용자 ID에 해당하는 특정 SSE Emitter 제거
   */
  public void removeEmitter(Long userId, SseEmitter emitter) {
    List<SseEmitter> userEmitters = emitters.get(userId);
    if (userEmitters != null) {
      userEmitters.remove(emitter);
      if (userEmitters.isEmpty()) {
        emitters.remove(userId);
      }
    }
  }

  /**
   * 사용자 ID에 해당하는 모든 SSE Emitter 반환
   */
  public List<SseEmitter> getEmitters(Long userId) {
    return emitters.getOrDefault(userId, new CopyOnWriteArrayList<>());
  }
}