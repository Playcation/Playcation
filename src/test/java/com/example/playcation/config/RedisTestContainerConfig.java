package com.example.playcation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class RedisTestContainerConfig {

  private static final GenericContainer<?> redisContainer;

  static {
    redisContainer = new GenericContainer<>(DockerImageName.parse("redis:6.2.6"))
        .withExposedPorts(6379);
    redisContainer.start();
  }

  @Bean
  public String redisHost() {
    return redisContainer.getHost();
  }

  @Bean
  public Integer redisPort() {
    return redisContainer.getMappedPort(6379);
  }
}
