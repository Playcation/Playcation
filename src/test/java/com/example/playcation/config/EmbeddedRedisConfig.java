package com.example.playcation.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import java.io.IOException;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import redis.embedded.RedisServer;

@EnableRedisRepositories
@TestConfiguration
public class EmbeddedRedisConfig {

  private RedisServer redisServer;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.host}")
  private String host;

  public EmbeddedRedisConfig() throws IOException {
    port = 6370;
    this.redisServer = new RedisServer(port);
  }

  @PostConstruct
  public void postConstruct() throws IOException {
    redisServer.start();
  }

  @PreDestroy
  public void preDestroy() throws IOException {
    redisServer.stop();
  }

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(
        host,
        port);
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory connectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    return template;
  }
}