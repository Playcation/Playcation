package com.example.playcation.cart.controller;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.dto.UpdatedCartGameResponseDto;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.util.TokenUtil;
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
  private final TokenUtil tokenUtil;

  @GetMapping
  public ResponseEntity<List<CartGameResponseDto>> getCartItems(
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    List<CartGameResponseDto> games = cartService.getCartItems(userId);

    return new ResponseEntity<>(games, HttpStatus.OK);
  }

  @PostMapping("/add/{gameId}")
  public ResponseEntity<UpdatedCartGameResponseDto> addGameToCart(@PathVariable Long gameId,
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    UpdatedCartGameResponseDto updatedCart = cartService.addGameToCart(userId, gameId);
    return new ResponseEntity<>(updatedCart, HttpStatus.OK);
  }

  @PostMapping("/delete/{gameId}")
  public ResponseEntity<UpdatedCartGameResponseDto> deleteGameFromCart(@PathVariable Long gameId,
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    UpdatedCartGameResponseDto updatedCart = cartService.deleteGameFromCart(userId, gameId);
    return new ResponseEntity<>(updatedCart, HttpStatus.OK);
  }

  @DeleteMapping("/remove")
  public ResponseEntity<String> deleteCart(
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    cartService.removeCart(userId);
    return new ResponseEntity<>("장바구니 삭제", HttpStatus.OK);
  }
}
