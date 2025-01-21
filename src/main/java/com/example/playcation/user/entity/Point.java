package com.example.playcation.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`point`")
public class Point {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private BigDecimal paidPoint;

  private BigDecimal freePoint;

  private Boolean isGetFreePoint;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  public Point(User user){
    this.paidPoint = BigDecimal.ZERO;
    this.freePoint = BigDecimal.ZERO;
    this.isGetFreePoint = false;
    this.user = user;
  }

  public BigDecimal getFreePoint(User user){
    this.freePoint = this.freePoint.add(user.getGrade().getFreePoint());
    isGetFreePoint = true;
    return this.freePoint.add(this.paidPoint);
  }

  public void updatePaidPoint(BigDecimal paidPoint) {
    this.paidPoint = paidPoint;
  }

  public void updateFreePoint(BigDecimal freePoint) {
    this.freePoint = freePoint;
  }

}
