package com.example.playcation.order.repository;

import com.example.playcation.order.entity.Profit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfitRepository extends JpaRepository<Profit, Long> {
  Profit findByUserId(Long userId);
}
