package com.example.playcation.coupon.entity;

import com.example.playcation.enums.CuponType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "`coupon`")
public class Coupon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private Long stock;

  private BigDecimal rate;

  @Enumerated(value = EnumType.STRING)
  private CuponType cuponType;
}
