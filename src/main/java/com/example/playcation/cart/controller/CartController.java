package com.example.playcation.cart.controller;

import com.example.playcation.cart.dto.CartResponseDto;
import com.example.playcation.cart.dto.UpdatedCartResponseDto;
import com.example.playcation.cart.entity.Cart;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.util.TokenUtil;
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
public class CartController {

  private final CartService cartService;
  private final TokenUtil tokenUtil;

  @PostMapping
  public ResponseEntity<Cart> createCart(
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    Cart cart = cartService.createCart(userId);
    return new ResponseEntity<>(cart, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<CartResponseDto> getCartItems(
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    CartResponseDto games = cartService.getCartItems(userId);

    return new ResponseEntity<>(games, HttpStatus.OK);
  }

  @PostMapping("/add/{gameId}")
  public ResponseEntity<UpdatedCartResponseDto> addGameToCart(@PathVariable Long gameId,
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    UpdatedCartResponseDto updatedCart = cartService.addGameToCart(userId, gameId);
    return new ResponseEntity<>(updatedCart, HttpStatus.OK);
  }

  @PostMapping("/delete/{gameId}")
  public ResponseEntity<UpdatedCartResponseDto> deleteGameFromCart(@PathVariable Long gameId,
      @RequestHeader("Authorization") String authorizationHeader) {
    Long userId = tokenUtil.findUserByToken(authorizationHeader);
    UpdatedCartResponseDto updatedCart = cartService.deleteGameFromCart(userId, gameId);
    return new ResponseEntity<>(updatedCart, HttpStatus.OK);
  }

}
