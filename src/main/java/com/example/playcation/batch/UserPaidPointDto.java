package com.example.playcation.batch;

import com.example.playcation.user.entity.User;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPaidPointDto {

  private final User user;
  private final BigDecimal total;
  private final BigDecimal userFreePoint;
}
