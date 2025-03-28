package com.example.playcation.order.repository;


import com.example.playcation.order.entity.PayoutLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutLogRepository extends JpaRepository<PayoutLog, Long> {
}
