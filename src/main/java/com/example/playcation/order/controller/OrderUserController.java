package com.example.playcation.order.controller;

import com.example.playcation.common.PagingDto;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.game.dto.GameSimpleResponseDto;
import com.example.playcation.order.dto.OrderProceedResponseDto;
import com.example.playcation.order.dto.OrderResponseDto;
import com.example.playcation.order.dto.RefundRequestDto;
import com.example.playcation.order.dto.RefundResponseDto;
import com.example.playcation.order.service.OrderUserService;
import com.example.playcation.util.JWTUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderUserController {

  private final OrderUserService orderService;
  private final JWTUtil jwtUtil;

  /**
   * 현재 로그인한 유저의 장바구니로 주문 정보 검색
   */
  @GetMapping("/proceed")
  public ResponseEntity<OrderProceedResponseDto> createOrder(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    OrderProceedResponseDto dto = orderService.createOrder(
        jwtUtil.findUserByToken(authorizationHeader));
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  /**
   * 결제에 성공한 주문 건에 대한 주문 정보 저장
   */
  @PostMapping("/success")
  public ResponseEntity<OrderResponseDto> completeOrder(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestParam String orderId
  ) {
    OrderResponseDto dto = orderService.completeOrder(
        jwtUtil.findUserByToken(authorizationHeader), orderId);
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
  }

  /**
   * 주문 단건 조회
   *
   * @param orderId 주문 식별자
   */
  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponseDto> findOrder(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable UUID orderId
  ) {
    OrderResponseDto responseDto = orderService.findOrder(
        jwtUtil.findUserByToken(authorizationHeader), orderId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 주문 다건 조회 페이징(최대 10, 기본 페이지 번호는 0)
   *
   * @param page 페이지 번호 (기본 0)
   */
  @GetMapping
  public ResponseEntity<PagingDto<OrderResponseDto>> findAllOrders(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    PagingDto<OrderResponseDto> pagingDto = orderService.findAllOrders(page, size,
        jwtUtil.findUserByToken(authorizationHeader));

    return new ResponseEntity<>(pagingDto, HttpStatus.OK);
  }

  /**
   * 주문 내 게임 단건 환불
   *
   * @param requestDto 주문 상세 id, 비밀번호, 환불 메세지
   * @param orderId    주문 식별자
   */
  @PostMapping("/{orderId}/refund")
  public ResponseEntity<RefundResponseDto> refundOrder(
      @Valid @RequestBody RefundRequestDto requestDto,
      @PathVariable UUID orderId,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    RefundResponseDto responseDto = orderService.refundOrder(
        jwtUtil.findUserByToken(authorizationHeader), orderId, requestDto);

    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  /**
   * 가장 최근의 주문 한 건의 게임 목록을 반환
   */
  @GetMapping("/latest")
  public ResponseEntity<List<GameSimpleResponseDto>> findLatestOrder(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    List<GameSimpleResponseDto> responseDtos = orderService.findLatestOrder(
        jwtUtil.findUserByToken(authorizationHeader));

    return new ResponseEntity<>(responseDtos, HttpStatus.OK);
  }
}
