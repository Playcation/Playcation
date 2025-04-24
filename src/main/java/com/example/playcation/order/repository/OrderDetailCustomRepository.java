package com.example.playcation.order.repository;


import java.math.BigDecimal;

public interface OrderDetailCustomRepository {

  public BigDecimal settleProfit(Long managerId);
}
