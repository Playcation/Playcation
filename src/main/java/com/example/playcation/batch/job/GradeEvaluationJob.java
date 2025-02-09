package com.example.playcation.batch.job;

import com.example.playcation.batch.PointWithUserDto;
import com.example.playcation.batch.UserPaidPointDto;
import com.example.playcation.enums.Grade;
import com.example.playcation.order.repository.OrderRepository;
import com.example.playcation.user.entity.Point;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.PointRepository;
import com.example.playcation.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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

@Configuration
@RequiredArgsConstructor
public class GradeEvaluationJob {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;

  private final UserRepository userRepository;
  private final PointRepository pointRepository;
  private final OrderRepository orderRepository;

  @Bean
  public Job GradeEvaluate() {

    return new JobBuilder("GradeEvaluationJob", jobRepository)
        .start(calculateGrade())
        .next(rewardByGrade())
        .build();
  }

  @Bean
  public Step calculateGrade() {

    return new StepBuilder("calculateGrade", jobRepository)
        .<UserPaidPointDto, User>chunk(100, platformTransactionManager)
        .reader(calculateGradeReader())
        .processor(calculateGradeProcessor())
        .writer(calculateGradeWriter())
        .build();
  }

  @Bean
  public RepositoryItemReader<UserPaidPointDto> calculateGradeReader() {

    return new RepositoryItemReaderBuilder<UserPaidPointDto>()
        .name("calculateGrade")
        .pageSize(100)
        .methodName("findAllPaidPointGroupByUser")
        .sorts(Map.of("user", Direction.ASC))
        .repository(orderRepository)
        .build();
  }

  @Bean
  public ItemProcessor<UserPaidPointDto, User> calculateGradeProcessor() {

    return new ItemProcessor<UserPaidPointDto, User>() {
      @Override
      public User process(UserPaidPointDto item) throws Exception {

        int point = item.getTotal().subtract(item.getUserFreePoint()).intValue();
        User user = item.getUser();

        if (point > Grade.VVIP.getGradeCriteria().intValue()) {
          user.updateGrade(Grade.VVIP);
        } else if (point > Grade.VIP.getGradeCriteria().intValue()) {
          user.updateGrade(Grade.VIP);
        } else {
          user.updateGrade(Grade.NORMAL);
        }

        return user;
      }
    };
  }

  @Bean
  public RepositoryItemWriter<User> calculateGradeWriter() {

    return new RepositoryItemWriterBuilder<User>()
        .repository(userRepository)
        .methodName("save")
        .build();
  }

  @Bean
  public Step rewardByGrade() {

    return new StepBuilder("rewardByGrade", jobRepository)
        .<PointWithUserDto, Point>chunk(100, platformTransactionManager)
        .reader(rewardByGradeReader())
        .processor(rewardByGradeProcessor())
        .writer(rewardByGradeWriter())
        .build();
  }

  @Bean
  public RepositoryItemReader<PointWithUserDto> rewardByGradeReader() {

    return new RepositoryItemReaderBuilder<PointWithUserDto>()
        .name("rewardByGrade")
        .pageSize(100)
        .methodName("findAllPointAndUser")
        .sorts(Map.of("id", Direction.ASC))
        .repository(pointRepository)
        .build();
  }

  @Bean
  public ItemProcessor<PointWithUserDto, Point> rewardByGradeProcessor() {

    return new ItemProcessor<PointWithUserDto, Point>() {
      @Override
      public Point process(PointWithUserDto item) throws Exception {

        BigDecimal plusPoint = item.getUser().getGrade().getFreePoint().multiply(BigDecimal.TEN);
        Point point = item.getPoint();
        point.updateFreePoint(point.getFreePoint().add(plusPoint));

        return point;
      }
    };
  }

  @Bean
  public RepositoryItemWriter<Point> rewardByGradeWriter() {

    return new RepositoryItemWriterBuilder<Point>()
        .repository(pointRepository)
        .methodName("save")
        .build();
  }
}
