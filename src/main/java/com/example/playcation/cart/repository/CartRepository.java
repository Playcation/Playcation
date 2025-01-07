package com.example.playcation.cart.repository;

import com.example.playcation.cart.entity.Cart;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.game.entity.Game;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> findByUserId(Long userId);

  default Cart findCartByUserIdOrElseThrow(Long userId) {
    return findByUserId(userId)
        .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));
  }

  @Query("select c from Cart c where c.user.id = :userId")
  List<Game> findAllById(Long userId);
}