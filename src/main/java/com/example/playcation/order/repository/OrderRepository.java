package com.example.playcation.order.repository;

import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.OrderErrorCode;
import com.example.playcation.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  /**
   * id에 해당하는 Order 반환, 없을시 커스텀 예외
   *
   * @param id 주문 식별자
   * @return 식별된 Order
   */
  default Order findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(OrderErrorCode.NOT_FOUND_ORDER));
  }
}
