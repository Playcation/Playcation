package com.example.playcation.cart.service;

import com.example.playcation.cart.dto.CartResponseDto;
import com.example.playcation.cart.dto.UpdatedCartResponseDto;
import com.example.playcation.cart.entity.Cart;
import com.example.playcation.cart.repository.CartRepository;
import com.example.playcation.exception.CartErrorCode;
import com.example.playcation.exception.NoAuthorizedException;
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

  // 장바구니 생성
  public Cart createCart(Long userId) {
    // 사용자 확인
    User user = userRepository.findByIdOrElseThrow(userId);

    // 이미 장바구니가 존재하는지 확인
    if (cartRepository.findByUserId(userId).isPresent()) {
      throw new NoAuthorizedException(CartErrorCode.EXIST_CART);
    }
    // 새로운 장바구니 생성
    Cart cart = Cart.createCart(user);

    // 저장 및 반환
    return cartRepository.save(cart);
  }

  public CartResponseDto getCartItems(Long userId) {

    Cart cart = cartRepository.findCartByUserIdOrElseThrow(userId);
    List<Game> gameList = cartRepository.findAllById(cart.getId());
    List<CartResponseDto.GameInfo> gameDetails = gameList.stream()
        .map(game -> new CartResponseDto.GameInfo(
            game.getId(),
            game.getTitle(),
            game.getPrice()
        ))
        .toList();
    return new CartResponseDto(
        cart.getId(),
        cart.getUser().getId(),
        gameDetails
    );
  }

  // cart에 게임 추가
  public UpdatedCartResponseDto addGameToCart(Long userId, Long gameId) {

    Game game = gameRepository.findByIdOrElseThrow(gameId);
    Cart cart = cartRepository.findCartByUserIdOrElseThrow(userId);

    // 장바구니에 게임 추가 (중복 방지)
    if (!cart.getGames().contains(game)) {
      cart.getGames().add(game);
    }

    // 변경된 장바구니 저장
    Cart savedCart = cartRepository.save(cart);

    List<Game> updatedGameList = cartRepository.findAllById(savedCart.getId());
    List<UpdatedCartResponseDto.GameInfo> gameDetails = updatedGameList.stream()
        .map(addedGame -> new UpdatedCartResponseDto.GameInfo(
            addedGame.getId(),
            addedGame.getTitle(),
            addedGame.getPrice()
        ))
        .toList();
    return new UpdatedCartResponseDto(
        cart.getId(),
        cart.getUser().getId(),
        gameDetails
    );
  }

  public UpdatedCartResponseDto deleteGameFromCart(Long userId, Long gameId) {

    Game game = gameRepository.findByIdOrElseThrow(gameId);
    Cart cart = cartRepository.findCartByUserIdOrElseThrow(userId);

    // 장바구니에 게임 추가 (중복 방지)
    if (!cart.getGames().contains(game)) {
      cart.getGames().remove(game);
    }

    // 변경된 장바구니 저장
    Cart savedCart = cartRepository.save(cart);

    List<Game> updatedGameList = cartRepository.findAllById(savedCart.getId());
    List<UpdatedCartResponseDto.GameInfo> gameDetails = updatedGameList.stream()
        .map(addedGame -> new UpdatedCartResponseDto.GameInfo(
            addedGame.getId(),
            addedGame.getTitle(),
            addedGame.getPrice()
        ))
        .toList();
    return new UpdatedCartResponseDto(
        cart.getId(),
        cart.getUser().getId(),
        gameDetails
    );
  }
}
