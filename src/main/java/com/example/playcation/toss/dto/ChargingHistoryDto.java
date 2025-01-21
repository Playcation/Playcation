package com.example.playcation.toss.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargingHistoryDto {

  private Long paymentHistoryId;

  @NotNull
  private BigDecimal amount;

  @NotNull
  private String orderName;

  private boolean isPaySuccessYN;

  private LocalDateTime createdAt;
}
