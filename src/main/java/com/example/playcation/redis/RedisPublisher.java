package com.example.playcation.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


/**
 * RedisPublisher는 Redis의 Pub/Sub 기능을 이용하여 메시지를 발행(Publish)하는 역할함
 * 메시지는 특정 채널(topic)에 발행되며, 이 채널을 구독하고 있는 모든 클라이언트(예: RedisSubscriber)에게 전달된다
 */
@Component
@RequiredArgsConstructor
public class RedisPublisher {

  // RedisTemplate은 Redis 서버와의 통신을 담당하며, 여기서는 String 타입의 키와 값을 사용
  private final RedisTemplate<String, String> redisTemplate;

  // ChannelTopic 객체는 Redis Pub/Sub에서 사용할 채널 이름을 캡슐화한다(보안을 위해)
  private final ChannelTopic topic;

  /**
   * 주어진 메시지를 Redis의 특정 채널에 발행
   * @param message 발행할 메시지 (예: "123 : [게임 제목]에 새로운 리뷰가 작성되었습니다.")
   */
  public void publish(String message) {
    // convertAndSend 메서드를 통해 topic에 해당하는 채널로 메시지를 발행합니다.
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }
}
