package com.example.playcation.toss.controller;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.config.TossPaymentConfig;
import com.example.playcation.toss.dto.PaymentDto;
import com.example.playcation.toss.dto.PaymentFailDto;
import com.example.playcation.toss.dto.PaymentResDto;
import com.example.playcation.toss.entity.Payment;
import com.example.playcation.toss.mapper.PaymentMapper;
import com.example.playcation.toss.service.PaymentService;
import com.example.playcation.util.JWTUtil;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final TossPaymentConfig tossPaymentConfig;
  private final PaymentService paymentService;
  private final JWTUtil jwtUtil;
  private final PaymentMapper mapper;

  @PostMapping("/toss")
  public ResponseEntity requestTossPayment(@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader, @RequestBody @Valid PaymentDto paymentReqDto) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    PaymentResDto paymentResDto = paymentService.requestTossPayment(paymentReqDto.toEntity(), userId).toPaymentResDto();
    paymentResDto.updateSuccessUrl(paymentReqDto.getYourSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : paymentReqDto.getYourSuccessUrl());
    paymentResDto.updateFailUrl(paymentReqDto.getYourFailUrl() == null ? tossPaymentConfig.getFailUrl() : paymentReqDto.getYourFailUrl());
    return ResponseEntity.ok().body(new ResponseEntity<>(paymentResDto, HttpStatus.OK));
  }

  @GetMapping("/toss/success")
  public ResponseEntity tossPaymentSuccess(
      @RequestParam String paymentKey,
      @RequestParam String orderId,
      @RequestParam BigDecimal amount
  ) {
    return ResponseEntity.ok().body(new ResponseEntity<>(paymentService.tossPaymentSuccess(paymentKey, orderId, amount), HttpStatus.OK));
  }

  @GetMapping("/toss/fail")
  public ResponseEntity tossPaymentFail(
      @RequestParam String code,
      @RequestParam String message,
      @RequestParam String orderId
  ) {
    paymentService.tossPaymentFail(code, message, orderId);
    return ResponseEntity.ok().body(new ResponseEntity<>(
        PaymentFailDto.builder()
            .errorCode(code)
            .errorMessage(message)
            .orderId(orderId)
            .build(),
        HttpStatus.OK
    ));
  }

  @PostMapping("/toss/cancel/point")
  public ResponseEntity tossPaymentCancelPoint(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestParam String paymentKey,
      @RequestParam String cancelReason
  ) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    return ResponseEntity.ok().body(new ResponseEntity<>(
        paymentService.cancelPaymentPoint(userId, paymentKey, cancelReason), HttpStatus.OK));
  }

  @GetMapping("/history")
  public ResponseEntity getChargingHistory(@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestParam(defaultValue = "0") int page) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    Page<Payment> chargingHistory = paymentService.findAllChargingHistories(userId, page);
    return new ResponseEntity<>(mapper.chargingHistoryToChargingHistoryResponse(chargingHistory.getContent()), HttpStatus.OK);
  }
}
