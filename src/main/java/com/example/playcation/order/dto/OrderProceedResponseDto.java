package com.example.playcation.order.dto;


import com.example.playcation.cart.dto.CartGameResponseDto;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderProceedResponseDto {

  private final List<CartGameResponseDto> games;

  private final BigDecimal total;

}
