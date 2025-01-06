package com.example.playcation.order.entity;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.OrderStatus;
import com.example.playcation.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "`order`")
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  private BigDecimal totalPrice;

  @Enumerated(value = EnumType.STRING)
  private OrderStatus status;
}
