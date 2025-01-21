package com.example.playcation.batch.job;

import com.example.playcation.coupon.entity.CouponUser;
import com.example.playcation.coupon.repository.CouponUserRepository;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
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
        .<CouponUser, CouponUser>chunk(10, platformTransactionManager)
        .allowStartIfComplete(true)
        .reader(reader())
        .writer(writer(couponUserRepository))
        .build();
  }

  @Bean
  public RepositoryItemReader<CouponUser> reader() {

    return new RepositoryItemReaderBuilder<CouponUser>()
        .name("findCouponUserAndDelete")
        .pageSize(10)
        .methodName("findAllByExpiredDateIsBefore")
        .arguments(LocalDate.now())
        .sorts(Map.of("id", Direction.ASC))
        .repository(couponUserRepository)
        .build();
  }

  @Bean
  public RepositoryItemWriter<CouponUser> writer(CouponUserRepository couponUserRepository) {

    return new RepositoryItemWriterBuilder<CouponUser>()
        .repository(couponUserRepository)
        .methodName("delete")
        .build();
  }
}

