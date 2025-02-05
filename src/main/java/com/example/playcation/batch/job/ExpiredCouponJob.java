package com.example.playcation.batch.job;

import com.example.playcation.coupon.repository.CouponUserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 유효 기간이 지난 쿠폰을 삭제하는 batch Job
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExpiredCouponJob {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final CouponUserRepository couponUserRepository;

  @Bean
  public Job deleteExpiredCouponUser() {

    return new JobBuilder("deleteExpiredCouponUser", jobRepository)
        .start(firstStep())
        .build();
  }

  @Bean
  public Step firstStep() {

    return new StepBuilder("findCouponUserAndDelete", jobRepository)
        .<Long, Long>chunk(100, platformTransactionManager)
        .allowStartIfComplete(true)
        .reader(reader())
        .writer(writer(couponUserRepository))
        .build();
  }

  @Bean
  public RepositoryItemReader<Long> reader() {

    return new RepositoryItemReaderBuilder<Long>()
        .name("findCouponUserAndDelete")
        .pageSize(100)
        .methodName("findAllIdsByExpiredDateIsBefore")
        .arguments(LocalDate.now())
        .sorts(Map.of("id", Direction.ASC))
        .repository(couponUserRepository)
        .build();
  }

  @Bean
  public ItemWriter<Long> writer(CouponUserRepository couponUserRepository) {

    return items -> couponUserRepository.deleteAllByIdInBatch(new ArrayList<>(items.getItems()));
  }
}

