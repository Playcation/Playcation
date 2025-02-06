package com.example.playcation.cart.repository;

import com.example.playcation.cart.entity.Cart;
import com.example.playcation.exception.CartErrorCode;
import com.example.playcation.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

  void deleteAllByUserId(Long userId);

  void deleteCartById(Long cartId);

  Optional<Cart> findByUserIdAndGameId(Long userId, Long gameId);

  List<Cart> findAllByUserId(Long userId);

  default Cart findCartByUserIdAndGameIdOrElseThrow(Long userId, Long gameId) {
    return findByUserIdAndGameId(userId, gameId)
        .orElseThrow(() -> new NotFoundException(CartErrorCode.NO_GAME_IN_CART));
  }

  int countByUserId(Long userId);
}