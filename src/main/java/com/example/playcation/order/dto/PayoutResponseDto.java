package com.example.playcation.order.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PayoutResponseDto {

  private final Long id;

  private final BigDecimal amount;

  private final BigDecimal previous;

  private final BigDecimal current;
}
