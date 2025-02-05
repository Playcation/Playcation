package com.example.playcation.order.entity;

import com.example.playcation.enums.OrderStatus;
import com.example.playcation.game.entity.Game;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "`order_detail`")
public class OrderDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "`game_id`")
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "`order_id`")
  private Order order;

  @Column(name = "`price`")
  private BigDecimal price;

  @Column(name = "`status`")
  @Enumerated(value = EnumType.STRING)
  private OrderStatus status;

  @OneToOne
  @JoinColumn(name = "`refund_id`")
  private Refund refund;

  public void updateStatus(OrderStatus status) {
    this.status = status;
  }

  public void updateRefund(Refund refund) {
    this.refund = refund;
  }
}
