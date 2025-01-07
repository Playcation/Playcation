package com.example.playcation.cart.controller;

import com.example.playcation.cart.dto.CartGameDto;
import com.example.playcation.cart.dto.UpdatedCartResponseDto;
import com.example.playcation.cart.entity.Cart;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.util.JwtTokenProvider;
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
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartContorller {

  private final CartService cartService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping
  public ResponseEntity<Cart> createCart(
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = findUserByToken(authorizationHeader);
    Cart cart = cartService.createCart(userId);
    return new ResponseEntity<>(cart, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<CartGameDto> getCartItems(
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = findUserByToken(authorizationHeader);
    CartGameDto games = cartService.getCartItems(userId);

    return new ResponseEntity<>(games, HttpStatus.OK);
  }

  @PostMapping("/add/{gameId}")
  public ResponseEntity<UpdatedCartResponseDto> addGameToCart(@PathVariable Long gameId,
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = findUserByToken(authorizationHeader);
    UpdatedCartResponseDto updatedCart = cartService.addGameToCart(userId, gameId);
    return new ResponseEntity<>(updatedCart, HttpStatus.OK);
  }

  @PostMapping("/delete/{gameId}")
  public ResponseEntity<UpdatedCartResponseDto> deleteGameFromCart(@PathVariable Long gameId,
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = findUserByToken(authorizationHeader);
    UpdatedCartResponseDto updatedCart = cartService.deleteGameFromCart(userId, gameId);
    return new ResponseEntity<>(updatedCart, HttpStatus.OK);
  }


  // 토큰에서 유저id를 찾아주는 메서드
  public Long findUserByToken(String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "").trim();
    return Long.parseLong(jwtTokenProvider.getUserId(token));
  }
}
