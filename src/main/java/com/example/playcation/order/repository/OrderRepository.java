package com.example.playcation.order.repository;

import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.OrderErrorCode;
import com.example.playcation.order.entity.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  /**
   * id에 해당하는 Order 반환, 없을시 커스텀 예외
   *
   * @param id 주문 식별자
   * @return 식별된 Order
   */
  default Order findByIdOrElseThrow(UUID id) {
    return findById(id).orElseThrow(() -> new NotFoundException(OrderErrorCode.NOT_FOUND_ORDER));
  }

  Page<Order> findAllByUserId(Long userId, Pageable pageable);

  Order findFirstByUserIdOrderByCreatedAtDesc(Long userId);

  boolean existsByIdAndUserId(UUID id, Long userId);
}
