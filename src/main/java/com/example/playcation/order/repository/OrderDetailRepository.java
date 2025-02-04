package com.example.playcation.order.repository;


import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.OrderErrorCode;
import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

  /**
   * 주문 id와 연결된 모든 주문 상세를 찾음
   */
  List<OrderDetail> findAllByOrderId(UUID orderId);

  /**
   * 주문 id 목록에 해당하는 모든 주문 상세를 찾음
   */
  List<OrderDetail> findAllByOrderIn(List<Order> orderIds);

  Page<OrderDetail> findAllByGameId(Long gameId, Pageable pageable);

  /**
   * OrderDetail id로 주문 상세 정보 단건을 찾음
   *
   * @apiNote 없을 시 커스텀 예외 throw
   */
  default OrderDetail findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() ->
        new NotFoundException(OrderErrorCode.NOT_FOUND_ORDER_DETAIL));
  }

  boolean existsByIdAndOrderId(Long id, UUID orderId);
}
