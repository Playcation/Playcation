package com.example.playcation.cart.dto;


import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 장바구니 내 게임 전체 조회 시 개별 게임 정보 Dto
 */
@Getter
@AllArgsConstructor
public class CartGameResponseDto {

  private Long id;
  private String title;
  private BigDecimal price;

}