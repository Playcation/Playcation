package com.example.playcation.cart.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.dto.UpdatedCartGameResponseDto;
import com.example.playcation.cart.entity.Cart;
import com.example.playcation.cart.repository.CartRepository;
import com.example.playcation.enums.GameStatus;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CartServiceTest {

  @InjectMocks
  private CartService cartService;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * 장바구니 아이템 조회 테스트
   */
  @Test
  void findCartItemsTest() {
    // Mock 데이터
    Long userId = 1L;
    User user = new User("a@a.com", "q1w2e3r4!!", "jina", Role.USER, Social.NORMAL);
    Game game = new Game(user, "Game1", "rpg", BigDecimal.valueOf(10000), "game1 description",
        GameStatus.ON_SAL, "iamge1.png");
    Cart cart = new Cart(1L, user, game);

    when(cartRepository.findAllByUserId(userId)).thenReturn(List.of(cart));

    // 실행
    List<CartGameResponseDto> result = cartService.findCartItems(userId);

    // 검증
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Game1", result.get(0).getTitle());
    verify(cartRepository, times(1)).findAllByUserId(userId);
  }

  /**
   * 장바구니에 게임 추가 테스트 (성공)
   */
  @Test
  void addGameToCartTest_Success() {
    // Mock 데이터
    Long userId = 1L;
    Long gameId = 1L;
    String auth = "USER";

    User user = new User("a@a.com", "q1w2e3r4!!", "jina", Role.USER, Social.NORMAL);
    Game game = new Game(user, "Game1", "rpg", BigDecimal.valueOf(10000), "game1 description",
        GameStatus.ON_SAL, "iamge1.png");
    Cart newCart = new Cart(1L, user, game);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    when(cartRepository.findByUserIdAndGameId(userId, gameId)).thenReturn(Optional.empty());
    when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

    // 실행
    UpdatedCartGameResponseDto result = cartService.addGameToCart(userId, gameId, auth);
    // 검증
    assertNotNull(result);
    assertEquals(gameId, result.getGameId());
    verify(cartRepository, times(1)).save(any(Cart.class));
  }


  /**
   * 장바구니에 게임 추가 테스트 (중복)
   */
  @Test
  void addGameToCartTest_Duplicated() {
    Long userId = 1L;
    Long gameId = 1L;
    String auth = "USER";

    User user = new User("a@a.com", "q1w2e3r4!!", "jina", Role.USER, Social.NORMAL);
    Game game = new Game(user, "Game1", "rpg", BigDecimal.valueOf(10000), "game1 description",
        GameStatus.ON_SAL, "iamge1.png");
    Cart existingCart = new Cart(1L, user, game);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
    when(cartRepository.findByUserIdAndGameId(userId, gameId)).thenReturn(
        Optional.of(existingCart));

    // 실행 및 검증
    assertThrows(DuplicatedException.class, () -> cartService.addGameToCart(userId, gameId, auth));
    verify(cartRepository, never()).save(any(Cart.class));
  }

  /**
   * 장바구니에서 게임 삭제 테스트
   */
  @Test
  void deleteGameFromCartTest() {
    Long userId = 1L;
    Long gameId = 1L;
    Long cartId = 1L;

    User user = new User("a@a.com", "q1w2e3r4!!", "jina", Role.USER, Social.NORMAL);
    Game game = new Game(user, "Game1", "rpg", BigDecimal.valueOf(10000), "game1 description",
        GameStatus.ON_SAL, "iamge1.png");
    Cart cart = new Cart(cartId, user, game);

    when(cartRepository.findByUserIdAndGameId(userId, gameId)).thenReturn(Optional.of(cart));

    // 실행
    assertDoesNotThrow(() -> cartService.deleteGameFromCart(userId, gameId));

    // 검증
    verify(cartRepository, times(1)).deleteCartById(cartId);


  }

  /**
   * 장바구니 삭제 테스트
   */
  @Test
  void removeCartTest() {
    Long userId = 1L;

    // 실행
    cartService.removeCart(userId);

    // 검증
    verify(cartRepository, times(1)).deleteAllByUserId(userId);
  }
}
