package com.example.playcation.user.dto;

import com.example.playcation.user.entity.RegistManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistManagerResponseDto {

  private Long userId;

  private String imageUrl;

  private String title;

  private String description;

  private BigDecimal price;

  // 약관 동의 여부
  private Boolean termsAgreement;

  public static RegistManagerResponseDto toDto(RegistManager registManager){
    return new RegistManagerResponseDto(
        registManager.getUser().getId(),
        registManager.getMainPicture(),
        registManager.getTitle(),
        registManager.getDescription(),
        registManager.getPrice(),
        registManager.getTermsAgreement()
    );
  }
}
