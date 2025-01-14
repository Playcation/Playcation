package com.example.playcation.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.entity.Cart;
import com.example.playcation.cart.repository.CartRepository;
import com.example.playcation.enums.Role;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @InjectMocks
  private CartService cartService;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private UserRepository userRepository;

  private List<Cart> carts = new ArrayList<>();
  private Cart cart = Mockito.mock(Cart.class);

  void setCarts() {
    User user = Mockito.mock(User.class);
    Game game = Mockito.mock(Game.class);
    given(cart.getId()).willReturn(1L);
    given(cart.getUser()).willReturn(user);
    given(cart.getGame()).willReturn(game);
    carts.add(cart);
  }

  @Nested
  class AddGameToCart {

    @Test
    void AddGameToCartTest() {
      // given
      Game game = Mockito.mock(Game.class);
      User user = Mockito.mock(User.class);
      Cart cart = Mockito.mock(Cart.class);

      when(user.getRole()).thenReturn(Role.USER);

      // Mock Cart 객체의 동작 설정
      when(cart.getUser()).thenReturn(user);
      when(cart.getGame()).thenReturn(game);

      // Mock Repositories 설정
      when(userRepository.findByIdOrElseThrow(anyLong())).thenReturn(user);
      when(gameRepository.findByIdOrElseThrow(anyLong())).thenReturn(game);
      when(cartRepository.save(any(Cart.class))).thenReturn(cart);

      // When & Then
      assertDoesNotThrow(() -> {
        cartService.addGameToCart(cart.getUser().getId(), cart.getGame().getId(),
            user.getRole().name());
      });
    }
  }

  @Nested
  class FindCartItems {

    @Test
    void findCartItemsSuccessTest() {
      // when
      setCarts();
      when(cartRepository.findAllByUserId(anyLong())).thenReturn(carts);

      // then
      List<CartGameResponseDto> result = cartService.findCartItems(cart.getUser().getId());

      Assertions.assertThat(result).isNotEmpty();
      Assertions.assertThat(result.get(0).getId()).isEqualTo(cart.getGame().getId());
    }

    @Test
    void findCartItemsFailureTest() {
      setCarts();
      // when
      when(cartRepository.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

      // then
      List<CartGameResponseDto> result = cartService.findCartItems(cart.getUser().getId());
      assertThat(result).isEmpty();
    }
  }

  @Nested
  class deleteGameFromCart {

    @Test
    void deleteGameFromCartSuccessTest() {
      // Mock 객체 설정
      setCarts(); // carts 리스트에 cart 추가
      given(cartRepository.findCartByUserIdAndGameIdOrElseThrow(anyLong(), anyLong()))
          .willReturn(cart); // cart 반환

      // 실행 및 검증
      assertDoesNotThrow(() ->
          cartService.deleteGameFromCart(cart.getUser().getId(), cart.getGame().getId())
      );

      // 호출 검증
      verify(cartRepository).findCartByUserIdAndGameIdOrElseThrow(anyLong(), anyLong());
      verify(cartRepository).deleteCartById(cart.getId());
    }

    @Test
    void deleteGameFromCartFailureTest() {
      // Mock 객체 설정
      setCarts(); // carts 리스트에 cart 추가
      given(cartRepository.findCartByUserIdAndGameIdOrElseThrow(anyLong(), anyLong()))
          .willThrow(new IllegalArgumentException("Cart not found"));
      // 실행 및 검증
      assertThrows(IllegalArgumentException.class, () ->
          cartService.deleteGameFromCart(cart.getUser().getId(), cart.getGame().getId())
      );

      // 호출 검증
      verify(cartRepository).findCartByUserIdAndGameIdOrElseThrow(anyLong(), anyLong());
    }
  }

  @Nested
  class removeCart {

    @Test
    void removeCartSuccessTest() {
      // Mock 데이터 설정
      setCarts();
      // 실행 및 검증
      assertDoesNotThrow(() -> cartService.removeCart(cart.getUser().getId()));

      // 호출 검증
      verify(cartRepository).deleteAllByUserId(cart.getUser().getId());
    }
  }
}
