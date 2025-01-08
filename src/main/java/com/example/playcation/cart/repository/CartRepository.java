package com.example.playcation.cart.repository;

import com.example.playcation.cart.entity.Cart;
import com.example.playcation.exception.CartErrorCode;
import com.example.playcation.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

  void deleteAllByUserId(Long userId);

  void deleteCartById(Long cartId);

  @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.game.id = :gameId")
  Optional<Cart> findByUserIdAndGameId(Long userId, Long gameId);

  @Query("select c from Cart c where c.user.id = :userId")
  List<Cart> findAllByUserId(Long userId);

  default Cart findCartByUserIdAndGameIdOrElseThrow(Long userId, Long gameId) {
    return findByUserIdAndGameId(userId, gameId)
        .orElseThrow(() -> new NotFoundException(CartErrorCode.NO_GAME_IN_CART));
  }

}