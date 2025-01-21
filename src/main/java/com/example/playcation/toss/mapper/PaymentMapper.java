package com.example.playcation.toss.mapper;

import com.example.playcation.toss.dto.ChargingHistoryDto;
import com.example.playcation.toss.entity.Payment;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;



@Mapper(componentModel = "Spring")
public interface PaymentMapper {

  default List<ChargingHistoryDto> chargingHistoryToChargingHistoryResponse(List<Payment> chargingHistories) {
    if (chargingHistories == null) {
      return null;
    }

    return chargingHistories.stream()
        .map(chargingHistory -> {
          return ChargingHistoryDto.builder()
              .paymentHistoryId(chargingHistory.getPaymentId())
              .amount(chargingHistory.getAmount())
              .orderName(chargingHistory.getOrderName())
              .createdAt(chargingHistory.getCreatedAt())
              .isPaySuccessYN(chargingHistory.isPaySuccessYN())
              .build();
        }).collect(Collectors.toList());
  }
}
