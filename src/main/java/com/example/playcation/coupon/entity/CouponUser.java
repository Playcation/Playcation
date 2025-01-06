package com.example.playcation.coupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CouponUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

}
