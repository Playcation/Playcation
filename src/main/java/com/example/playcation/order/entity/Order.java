package com.example.playcation.order.entity;

import com.example.playcation.common.BaseEntity;
import com.example.playcation.enums.OrderStatus;
import com.example.playcation.user.entity.User;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Entity
@Table(name = "`order`")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  private BigDecimal totalPrice;

  private BigDecimal freePoint;

  @Enumerated(value = EnumType.STRING)
  private OrderStatus status;

  public void successStatus() {
    this.status = OrderStatus.SUCCESS;
  }

  public void expireStatus() {
    this.status = OrderStatus.EXPIRED;
  }
}
