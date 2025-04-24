package com.example.playcation.batch.job;

import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.entity.Profit;
import com.example.playcation.order.repository.OrderDetailRepository;
import com.example.playcation.order.repository.ProfitRepository;
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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AddProfitJob {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final ProfitRepository profitRepository;
  private final OrderDetailRepository orderDetailRepository;

  @Bean
  public Job addProfit() {

    return new JobBuilder("addProfit", jobRepository)
        .start(firstStep())
        .build();
  }

  @Bean
  public Step firstStep() {

    return new StepBuilder("checkOrderAndAddProfit", jobRepository)
        .<OrderDetail, Long>chunk(100, platformTransactionManager)
        .reader(checkOrderAndAddProfitReader())
//        .processor()
//        .writer()
        .build();
  }

  /*
    TODO:
     - 게임별 정산 값을 GameProfit 에 적용
     - 총액 구해서 User 참조하는 UserProfit 에 업로드
      (UserProfit: 출금 완료 금액, 출금 가능한 금액 나눠서 저장)
 */
  @Bean
  public RepositoryItemReader<OrderDetail> checkOrderAndAddProfitReader() {

    return new RepositoryItemReaderBuilder<OrderDetail>()
        .name("checkOrderAndAddProfit")
        .pageSize(100)
        .methodName("findAllByOrderCreatedAtIsBeforeAndIsSettledIsFalse")
        .arguments(LocalDateTime.now().minusDays(2))
        .sorts(Map.of("id", Direction.ASC))
        .repository(orderDetailRepository)
        .build();
  }

  @Bean
  public ItemProcessor<OrderDetail, Profit> checkOrderAndAddProfitProcessor() {

    return new ItemProcessor<OrderDetail, Profit>() {
      @Override
      public Profit process(OrderDetail item) throws Exception {

        return null;
      }
    };
  }

  @Bean
  public RepositoryItemWriter<Profit> checkOrderAndAddProfitWriter() {

    return new RepositoryItemWriterBuilder<Profit>()
        .repository(profitRepository)
        .methodName("save")
        .build();
  }
}
