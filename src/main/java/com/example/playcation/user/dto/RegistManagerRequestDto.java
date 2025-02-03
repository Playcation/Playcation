package com.example.playcation.user.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class RegistManagerRequestDto {

//  @NotBlank
  private String title;

//  @NotBlank
  private String description;

//  @NotNull
  private BigDecimal price;

  // 약관 동의 여부
//  @NotNull
  private Boolean termsAgreement;

}
