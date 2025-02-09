package com.example.playcation;

import com.example.playcation.config.RedisTestContainerConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {
    RedisTestContainerConfig.class
}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PlaycationApplicationTests {

  @Test
  void contextLoads() {
  }

}
