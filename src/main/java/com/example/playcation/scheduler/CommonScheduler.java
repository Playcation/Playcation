package com.example.playcation.scheduler;

import com.example.playcation.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class CommonScheduler {

  private static final Long USER_DELETION_WAIT_TIME = 30L;

  private final UserService userService;

  // TODO: 스케줄러 설정 바꿔서 켜놓기!!
  //  정각: cron = "0 0 0 * * *"
  /**
   * 탈퇴 후 30일이 지난 유저 정보를 삭제
   */
  // @Scheduled(cron = "10 * * * * *")
  public void runDeleteExpiredUser() {
    userService.deleteExpiredUsers(USER_DELETION_WAIT_TIME);
  }
}
