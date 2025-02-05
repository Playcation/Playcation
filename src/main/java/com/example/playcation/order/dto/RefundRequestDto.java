package com.example.playcation.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefundRequestDto {

  @NotNull(message = "주문 상세 정보 id를 입력해주세요.")
  private Long orderDetailId;

  @NotEmpty(message = "비밀번호를 입력해주세요.")
  private String password;

  @NotEmpty(message = "환불 사유는 필수로 입력해야 합니다.")
  private String refundMessage;
}
