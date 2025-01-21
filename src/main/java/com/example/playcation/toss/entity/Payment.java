package com.example.playcation.toss.entity;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.PayType;
import com.example.playcation.toss.dto.PaymentResDto;
import com.example.playcation.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
    @Index(name = "idx_payment_member", columnList = "'user'"),
    @Index(name = "idx_payment_paymentKey", columnList = "paymentKey"),
})
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id", nullable = false, unique = true)
  private Long paymentId;

  @Column(nullable = false, name = "pay_type")
  @Enumerated(EnumType.STRING)
  private PayType payType;

  @Column(nullable = false, name = "pay_amount")
  private BigDecimal amount;

  @Column(nullable = false, name = "pay_name")
  private String orderName;

  @Column(nullable = false, name = "order_id")
  private String orderId;

  private boolean paySuccessYN;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "user_id")
  private User user;

  @Column
  private String paymentKey;

  @Column
  private String failReason;

  @Column
  private boolean cancelYN;

  @Column
  private String cancelReason;

  public PaymentResDto toPaymentResDto() {
    return PaymentResDto.builder()
        .payType(payType.getDescription())
        .amount(amount)
        .orderName(orderName)
        .orderId(orderId)
        .customerEmail(user.getEmail())
        .customerName(user.getName())
        .createdAt(String.valueOf(getCreatedAt()))
        .cancelYN(cancelYN)
        .failReason(failReason)
        .build();
  }

  public void updateUser (User user) {
    this.user = user;
  }

  public void updatePaymentKey(String paymentKey) {
    this.paymentKey = paymentKey;
  }

  public void updatePaySuccess(boolean paySuccessYN) {
    this.paySuccessYN = paySuccessYN;
  }

  public void updateFailReason(String failReason) {
    this.failReason = failReason;
  }

  public void updateCancelYN(boolean cancelYN) {
    this.cancelYN = cancelYN;
  }

  public void updateCancelReason(String cancelReason) {
    this.cancelReason = cancelReason;
  }
}
