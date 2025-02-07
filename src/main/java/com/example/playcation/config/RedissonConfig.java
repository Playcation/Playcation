package com.example.playcation.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

  @Value("${spring.data.redisson.host}")
  private String host;

  @Value("${spring.data.redisson.port}")
  private int port;

  @Value("${spring.data.redisson.password}")
  private String password;

  private static final String REDISSON_HOST_PREFIX = "redis://";

  @Bean
  public RedissonClient redissonClient() {
    RedissonClient redisson = null;
    Config config = new Config();
    config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port)
        .setPassword(password);
    redisson = Redisson.create(config);
    return redisson;
  }
}