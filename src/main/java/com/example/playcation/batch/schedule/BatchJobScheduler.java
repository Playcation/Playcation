package com.example.playcation.batch.schedule;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchJobScheduler {

  private final JobLauncher jobLauncher;
  private final JobRegistry jobRegistry;

  // TODO: 배포 전에 스케줄러 작동 켜놓기
  //  테스트할 때는 cron = "10 * * * * *"
  /**
   * 탈퇴 후 30일이 지난 유저 정보를 삭제하는 job<br>
   * 매일 오전 4시에 작동
   */
//  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void runDeleteExpiredUserJob() throws Exception {

    jobLauncher.run(jobRegistry.getJob("deleteExpiredUser"), new JobParameters());
  }
}
