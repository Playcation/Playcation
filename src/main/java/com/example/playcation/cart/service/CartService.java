package com.example.playcation.cart.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.dto.UpdatedCartGameResponseDto;
import com.example.playcation.cart.entity.Cart;
import com.example.playcation.cart.repository.CartRepository;
import com.example.playcation.exception.CartErrorCode;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

  private final GameRepository gameRepository;
  private final CartRepository cartRepository;
  private final UserRepository userRepository;


  public List<CartGameResponseDto> getCartItems(Long userId) {

    List<Cart> cartList = cartRepository.findAllById(userId);
    return cartList.stream()
        .map(cart -> new CartGameResponseDto(
            cart.getId(),
            cart.getGame().getTitle(),
            cart.getGame().getPrice()
        ))
        .toList();
  }

  // cart에 게임 추가
  public UpdatedCartGameResponseDto addGameToCart(Long userId, Long gameId) {
    User user = userRepository.findByIdOrElseThrow(userId);
    Game game = gameRepository.findByIdOrElseThrow(gameId);
    // 이미 장바구니가 존재하는지 확인
    if (cartRepository.findByUserIdAndGameId(userId, gameId).isPresent()) {
      throw new DuplicatedException(CartErrorCode.GAME_ALREADY_IN_CART);
    }
    // 장바구니에 게임 추가
    Cart newCart = Cart.builder()
        .user(user)
        .game(game)
        .build();

    // 변경된 장바구니 저장
    cartRepository.save(newCart);

    return UpdatedCartGameResponseDto.toDto(newCart);
  }

  public UpdatedCartGameResponseDto deleteGameFromCart(Long userId, Long gameId) {

    Cart cart = cartRepository.findCartByUserIdAndGameIdOrElseThrow(userId, gameId);

    // 장바구니에 게임 삭제
    cartRepository.delete(cart);

    // 변경된 장바구니 저장
    Cart savedCart = cartRepository.save(cart);

    // 변경된 장바구니 저장
    cartRepository.save(savedCart);

    return UpdatedCartGameResponseDto.toDto(savedCart);
  }

  public void removeCart(Long userId) {
    cartRepository.deleteAllByUserId(userId);
  }
}
