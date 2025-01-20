package com.example.playcation.toss.dto;

import com.example.playcation.toss.entity.Payment;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PagePaymentDto {

  private List<Payment> paymentList;

}
