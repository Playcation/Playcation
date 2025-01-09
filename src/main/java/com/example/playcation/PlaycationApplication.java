package com.example.playcation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PlaycationApplication {

  public static void main(String[] args) {
    SpringApplication.run(PlaycationApplication.class, args);
  }

}
