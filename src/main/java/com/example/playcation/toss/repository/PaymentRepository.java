package com.example.playcation.toss.repository;

import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.PaymentErrorCode;
import com.example.playcation.toss.entity.Payment;
import com.example.playcation.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByOrderId(String orderId);
  default Payment findByOrderIdOrElseThrow(String orderId) {
    Payment payment = findByOrderId(orderId).orElseThrow(() -> new NotFoundException(
        PaymentErrorCode.PAYMENT_NOT_FOUND));
    return payment;
  }

  Optional<Payment> findByPaymentKeyAndUser_Email(String paymentKey, String email);
  default Payment findByPaymentKeyAndUser_EmailOrElseThrow(String paymentKey, String email) {
    Payment payment = findByPaymentKeyAndUser_Email(paymentKey, email).orElseThrow(() -> new NotFoundException(
        PaymentErrorCode.PAYMENT_NOT_FOUND
    ));
    return payment;
  }

  Page<Payment> findAllByUser(User user, Pageable pageable);
}
