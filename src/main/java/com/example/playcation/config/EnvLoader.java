package com.example.playcation.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Map;

public class EnvLoader {
  private static final String ACTIVE_PROFILE = System.getProperty("spring.profiles.active", "local");

  private static final Dotenv dotenv = Dotenv.configure()
      .directory(ACTIVE_PROFILE.equals("local") ? "src/main/resources" : ".")  // ✅ 로컬이면 resources에서 로드, 운영이면 루트에서 로드
      .filename(".env." + ACTIVE_PROFILE)  // `.env.local`, `.env.test`, `.env.prod`
      .ignoreIfMissing()
      .load();

  static {
    // .env 변수들을 System Properties에 추가
    dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
  }

  public static String get(String key) {
    return dotenv.get(key);
  }

  public static void printEnvVariables() {
    System.out.println("Loaded Environment Variables:");
    dotenv.entries().forEach(entry -> System.out.println(entry.getKey() + "=" + entry.getValue()));
  }
}
