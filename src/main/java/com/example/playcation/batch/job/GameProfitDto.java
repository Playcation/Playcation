package com.example.playcation.batch.job;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class GameProfitDto {

  private String game;
  private BigDecimal totalProfit;
}
