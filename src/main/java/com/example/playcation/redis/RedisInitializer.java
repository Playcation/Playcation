package com.example.playcation.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisInitializer implements CommandLineRunner {

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void run(String... args){
//    redisTemplate.getConnectionFactory().getConnection().flushDb(); // 모든 데이터 삭제
//    System.out.println("Redis 초기화 완료");
  }
}