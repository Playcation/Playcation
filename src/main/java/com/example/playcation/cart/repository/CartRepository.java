package com.example.playcation.cart.repository;

import com.example.playcation.cart.entity.Cart;
import com.example.playcation.exception.CartErrorCode;
import com.example.playcation.exception.DuplicatedException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

  void deleteAllByUserId(Long userId);

  Optional<Cart> findByUserIdAndGameId(Long userId, Long gameId);

  default Cart findCartByUserIdAndGameIdOrElseThrow(Long userId, Long gameId) {
    return findByUserIdAndGameId(userId, gameId)
        .orElseThrow(() -> new DuplicatedException(CartErrorCode.GAME_ALREADY_IN_CART));
  }

  @Query("select c from Cart c where c.user.id = :userId")
  List<Cart> findAllById(Long userId);

}