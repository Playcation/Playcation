package com.example.playcation.order.controller;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.order.dto.PayoutRequestDto;
import com.example.playcation.order.dto.PayoutResponseDto;
import com.example.playcation.order.repository.PayoutLogRepository;
import com.example.playcation.order.repository.ProfitRepository;
import com.example.playcation.order.service.OrderManagerService;
import com.example.playcation.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/orders")
public class OrderManagerController {

  private final OrderManagerService orderManagerService;
  private final JWTUtil jwtUtil;

  /**
   * 수익금 출금 요청 메서드
   */
  @PostMapping("/payout")
  public ResponseEntity<PayoutResponseDto> requestPayout(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @Valid @RequestBody PayoutRequestDto dto
  ) {
    PayoutResponseDto responseDto = orderManagerService.requestPayout(
        jwtUtil.findUserByToken(authorizationHeader), dto);

    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }
}
