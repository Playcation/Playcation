package com.example.playcation.batch.job;

import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 탈퇴 후 30일이 지난 유저의 정보를 삭제하는 batch Job
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExpiredUserJob {

  private static final Long USER_DELETION_WAIT_TIME = 30L;
  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final UserRepository userRepository;
  private final UserService userService;

  @Bean
  public Job deleteExpiredUser() {

    return new JobBuilder("deleteExpiredUser", jobRepository)
        .start(firstStep())
        .build();
  }

  @Bean
  public Step firstStep() {

    return new StepBuilder("findUserAndDelete", jobRepository)
        .<User, User>chunk(10, platformTransactionManager)
        .allowStartIfComplete(true)
        .reader(reader())
        .processor(processor())
        .writer(writer(userRepository))
        .build();
  }

  @Bean
  public RepositoryItemReader<User> reader() {

    return new RepositoryItemReaderBuilder<User>()
        .name("findUserAndDelete")
        .pageSize(10)
        .methodName("findAllByDeletedAtIsNotNullAndDeletedAtIsBeforeAndNameIsNotNull")
        .arguments(LocalDateTime.now().minusDays(USER_DELETION_WAIT_TIME))
        .repository(userRepository)
        .sorts(Map.of("createdAt", Direction.DESC))
        .build();
  }

  @Bean
  public ItemProcessor<User, User> processor() {

    return new ItemProcessor<User, User>() {

      @Override
      public User process(User user) {

        userService.expire(user);
        return user;
      }
    };
  }

  @Bean
  public RepositoryItemWriter<User> writer(UserRepository userRepository) {

    return new RepositoryItemWriterBuilder<User>()
        .repository(userRepository)
        .methodName("save")
        .build();
  }
}

