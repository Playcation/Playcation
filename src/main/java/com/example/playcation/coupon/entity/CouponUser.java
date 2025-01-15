package com.example.playcation.coupon.entity;

import com.example.playcation.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class CouponUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "`user_id`")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "`coupon_id`")
  private Coupon coupon;

}
