package com.example.playcation.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class RedisTestContainerConfig {

  private static final GenericContainer<?> redisContainer;

  static {
    redisContainer = new GenericContainer<>(
        DockerImageName.parse("redis:7.4.2"))
        .withExposedPorts(6379);
    redisContainer.start();
    System.setProperty("spring.redis.host", redisContainer.getHost());
    System.setProperty("spring.redis.port", redisContainer.getFirstMappedPort().toString());
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisContainer.getHost(),
        redisContainer.getMappedPort(6379));
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
  }
}
