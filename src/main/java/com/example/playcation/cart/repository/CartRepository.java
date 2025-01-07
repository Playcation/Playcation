package com.example.playcation.cart.repository;

import com.example.playcation.cart.entity.Cart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> findByUserIdAndGameId(Long userId, Long gameId);

  void deleteAllByUserId(Long userId);


  // 특정 사용자와 게임으로 장바구니 항목을 조회하고 없으면 예외를 던지는 메서드
  default Cart findCartByUserIdAndGameIdOrElseThrow(Long userId, Long gameId) {
    return findByUserIdAndGameId(userId, gameId)
        .orElseThrow(() -> new IllegalArgumentException("해당 회원의 장바구니에 해당 게임이 없습니다."));
  }

  @Query("select c from Cart c where c.user.id = :userId")
  List<Cart> findAllById(Long userId);

}