package com.example.playcation.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisConnectionTester {

  private final StringRedisTemplate redisTemplate;

  public RedisConnectionTester(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void testConnection() {
    redisTemplate.opsForValue().set("testKey", "testValue");
    String value = redisTemplate.opsForValue().get("testKey");
    System.out.println("Redis 연결 테스트: " + value);
  }
}
