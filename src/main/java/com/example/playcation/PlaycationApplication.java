package com.example.playcation;

import com.example.playcation.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class PlaycationApplication {

  public static void main(String[] args) {
    EnvLoader.printEnvVariables();
    SpringApplication.run(PlaycationApplication.class, args);
  }

}
