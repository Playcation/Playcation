package com.example.playcation.toss.service;

import com.example.playcation.toss.dto.PaymentSuccessDto;
import com.example.playcation.toss.entity.Payment;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface PaymentService {

  Payment requestTossPayment(Payment payment, Long userId);

  PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId, BigDecimal amount);

  void tossPaymentFail(String code, String message, String orderId);

  Map cancelPaymentPoint(Long userId, String paymentKey, String cancelReason);

  Page<Payment> findAllChargingHistories(Long userId, int page);
}
