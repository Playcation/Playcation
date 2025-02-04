package com.example.playcation.cart.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.dto.UpdatedCartGameResponseDto;
import com.example.playcation.cart.entity.Cart;
import com.example.playcation.cart.repository.CartRepository;
import com.example.playcation.enums.Role;
import com.example.playcation.exception.CartErrorCode;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

  private final GameRepository gameRepository;
  private final CartRepository cartRepository;
  private final UserRepository userRepository;

  /**
   * 장바구니 내 게임 리스트 조회 Service 메서드
   *
   * @param userId
   * @return CartGameResponseDto 리스트
   */
  public List<CartGameResponseDto> findCartItems(Long userId) {

    List<Cart> cartList = cartRepository.findAllByUserId(userId);
    return cartList.stream()
        .map(cart -> new CartGameResponseDto(
            cart.getGame().getId(),
            cart.getGame().getImageUrl(),
            cart.getGame().getTitle(),
            cart.getGame().getPrice()
        ))
        .toList();
  }

  /**
   * 장바구니에 게임울 추가하는 Service 메서드
   *
   * @param userId
   * @param gameId
   * @return UpdatedCartGameResponseDto ( cart 엔티티와 필드 동일 )
   */
  @Transactional
  public UpdatedCartGameResponseDto addGameToCart(Long userId, Long gameId, String auth) {
    // User, game 조회 및 예외 처리
    User user = userRepository.findByIdOrElseThrow(userId);
    Game game = gameRepository.findByIdOrElseThrow(gameId);

    // 유저가 MANAGER 이고 추가하려는 게임의 관리자일 시
    if (auth.equals(Role.MANAGER.name()) && (user.isManagerOfGame(game))) {
      throw new NoAuthorizedException(CartErrorCode.NO_AUTHORIZED_CART);
    }
    // 이미 회원의 장바구니에 게임이 존재하는지 확인
    if (cartRepository.findByUserIdAndGameId(userId, gameId).isPresent()) {
      throw new DuplicatedException(CartErrorCode.GAME_ALREADY_IN_CART);
    }

    // 해당 회원의 장바구니에 게임 추가
    Cart newCart = Cart.builder()
        .user(user)
        .game(game)
        .build();

    // 변경된 장바구니 저장
    cartRepository.save(newCart);

    return UpdatedCartGameResponseDto.toDto(newCart);
  }

  /**
   * 장바구니에서 게임을 삭제하는 Service 메서드
   *
   * @param userId
   * @param gameId
   */
  @Transactional
  public void deleteGameFromCart(Long userId, Long gameId) {
    // 회원의 장바구니에 게임이 존재하는지 확인
    Cart cart = cartRepository.findCartByUserIdAndGameIdOrElseThrow(userId, gameId);

    // 장바구니에서 게임 삭제
    cartRepository.deleteCartById(cart.getId());

  }

  @Transactional
  public void removeCart(Long userId) {
    // 요청한 회원 id와 일치하는 장바구니 모두 삭제
    cartRepository.deleteAllByUserId(userId);
  }
}
