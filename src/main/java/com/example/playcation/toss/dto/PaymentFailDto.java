package com.example.playcation.toss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailDto {

  String errorCode;

  String errorMessage;

  String orderId;
}