//package com.example.playcation.config;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import java.io.IOException;
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Profile;
//import redis.embedded.RedisServer;
//
//@TestConfiguration
//@Profile("test")
//public class EmbeddedRedissonConfig {
//
//  private RedisServer redisServer;
//
//  @Value("${spring.data.redisson.port:6370}")
//  private int port;
//
//  @Value("${spring.data.redisson.host}")
//  private String host;
//
//  @Value("${spring.data.redisson.password}")
//  private String password;
//
//  @PostConstruct
//  public void startRedis() throws IOException {
//    redisServer = new RedisServer(port);
//    redisServer.start();
//  }
//
//  @PreDestroy
//  public void stopRedis() throws IOException {
//    if (redisServer != null) {
//      redisServer.stop();
//    }
//  }
//
//  private static final String REDISSON_HOST_PREFIX = "redis://";
//
//  @Bean
//  public RedissonClient redissonClient() {
//    RedissonClient redisson = null;
//    Config config = new Config();
//    config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port);
//    redisson = Redisson.create(config);
//    return redisson;
//  }
//}