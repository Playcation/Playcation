package com.example.playcation.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.ServerSocket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import java.io.IOException;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import redis.embedded.RedisServer;

@EnableRedisRepositories
@TestConfiguration
@Profile("test")
public class EmbeddedRedisConfig {

  private RedisServer redisServer;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.host}")
  private String host;

  @PostConstruct
  public void postConstruct() throws IOException {
    port = findAvailablePort();
    redisServer = RedisServer.newRedisServer().port(6370).setting("maxmemory 128M").build();
    try {
      redisServer.start();
    } catch (Exception e) {
      redisServer.stop();
      redisServer.start();
    }

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

  @Bean
  public int testPort() {
    return this.port;
  }

  private int findAvailablePort() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException("❌ Failed to find an available port for Redis", e);
    }
  }
}