package com.example.playcation.game.dto;

import com.example.playcation.order.dto.OrderDetailResponseDto;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ManagerGameDetailResponseDto {

  private final Long totalSoldQuantity;
  private final List<GameOrderDetail> salesHistory;

  public static ManagerGameDetailResponseDto toDto(List<OrderDetail> details,
      long totalSoldQuantity) {

    List<GameOrderDetail> list = new ArrayList<>();
    for (OrderDetail d : details) {
      OrderDetailResponseDto dto = OrderDetailResponseDto.toDto(d);
      User user = d.getOrder().getUser();
      list.add(new GameOrderDetail(
          user.getId(),
          user.getEmail(),
          user.getName(),
          dto
      ));
    }

    return new ManagerGameDetailResponseDto(totalSoldQuantity, list);
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  static class GameOrderDetail {

    private Long userId;
    private String userEmail;
    private String userName;
    private OrderDetailResponseDto content;
  }
}
