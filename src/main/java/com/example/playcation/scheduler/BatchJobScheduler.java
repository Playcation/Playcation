package com.example.playcation.scheduler;


import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class BatchJobScheduler {

  private final JobLauncher jobLauncher;
  private final JobRegistry jobRegistry;

  // TODO: 배포 전에 스케줄러 설정 바꿔서 작동 켜놓기
  //  매일 정각: cron = "0 0 0 * * *"
  /**
   * 유효 기간이 지난 쿠폰을 삭제하는 job<br>
   * 매일 정각에 실행
   */
//   @Scheduled(cron = "10 * * * * *", zone = "Asia/Seoul")
  public void runDeleteExpiredUserJob() throws Exception {

    jobLauncher.run(jobRegistry.getJob("deleteExpiredCouponUser"), new JobParameters());
  }

//  @Scheduled(cron = "0 0 2 1 * *", zone = "Asia/Seoul")
  public void runGradeEvaluationJob() throws Exception {

    // LocalDate.now().getMonth().toString()
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("month", LocalDateTime.now().toString())
        .toJobParameters();

    jobLauncher.run(jobRegistry.getJob("GradeEvaluationJob"), jobParameters);
  }
}
