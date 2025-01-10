package com.example.playcation.order.controller;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.order.dto.OrderResponseDto;
import com.example.playcation.order.service.OrderService;
import com.example.playcation.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;
  private final JWTUtil jwtUtil;

  /**
   * 현재 로그인한 유저의 장바구니로 주문 생성
   */
  @PostMapping
  public ResponseEntity<OrderResponseDto> createOrder(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    OrderResponseDto responseDto = orderService.createOrder(
        jwtUtil.findUserByToken(authorizationHeader));
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  /**
   * 주문 단건 조회
   *
   * @param orderId 주문 식별자
   */
  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponseDto> findOrder(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable Long orderId
  ) {
    OrderResponseDto responseDto = orderService.findOrder(
        jwtUtil.findUserByToken(authorizationHeader),
        orderId,
        jwtUtil.findAuthByToken(authorizationHeader));
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

}
