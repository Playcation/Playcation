package com.example.playcation.cart.controller;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.dto.UpdatedCartGameResponseDto;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.util.JWTUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;
  private final JWTUtil jwtUtil;

  /**
   * 회원의 장바구니 게임을 전체 조회하는 메서드
   *
   * @param authorizationHeader
   * @return CartGameResponseDto 리스트
   */
  @GetMapping
  public ResponseEntity<List<CartGameResponseDto>> findCartItems(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    List<CartGameResponseDto> games = cartService.findCartItems(userId);

    return new ResponseEntity<>(games, HttpStatus.OK);
  }

  /**
   * 장바구니에 게임울 추가하는 메서드
   *
   * @param gameId
   * @param authorizationHeader
   * @return UpdatedCartGameResponseDto ( cart 엔티티와 필드 동일 )
   */
  @PostMapping("/add/{gameId}")
  public ResponseEntity<UpdatedCartGameResponseDto> addGameToCart(@PathVariable Long gameId,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    String auth = jwtUtil.findAuthByToken(authorizationHeader);
    UpdatedCartGameResponseDto updatedCart = cartService.addGameToCart(userId, gameId, auth);
    return new ResponseEntity<>(updatedCart, HttpStatus.OK);
  }

  /**
   * 장바구니에서 특정 게임을 삭제하는 메서드
   *
   * @param gameId
   * @param authorizationHeader
   * @return String ( 게임 삭제 완료 메시지 )
   */
  @DeleteMapping("/delete/{gameId}")
  public ResponseEntity<String> deleteGameFromCart(@PathVariable Long gameId,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    cartService.deleteGameFromCart(userId, gameId);
    return new ResponseEntity<>("게임 삭제 완료", HttpStatus.OK);
  }

  /**
   * 회원이 자발적으로 장바구니를 삭제하는 메서드 이 메서드는 주문 과정과는 무관하며, 사용자가 장바구니를 비우려는 경우 호출됨
   *
   * @param authorizationHeader
   * @return String ( 장바구니 삭제 완료 메시지 )
   */
  @DeleteMapping("/remove")
  public ResponseEntity<String> deleteCartByUserRequest(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    cartService.removeCart(userId); // cart는 hard delete 됨
    return new ResponseEntity<>("장바구니 삭제 완료", HttpStatus.OK);
  }

  // 🚀 장바구니 개수 조회 API
  @GetMapping("/count")
  public ResponseEntity<Integer> getCartCount(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {
    Long userId = jwtUtil.findUserByToken(authorizationHeader);
    int count = cartService.getCartCount(userId);
    return new ResponseEntity<>(count, HttpStatus.OK);
  }
}
