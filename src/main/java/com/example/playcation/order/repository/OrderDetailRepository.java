package com.example.playcation.order.repository;


import com.example.playcation.order.entity.Order;
import com.example.playcation.order.entity.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

  List<OrderDetail> findAllByOrderId(Long orderId);

  List<OrderDetail> findAllByOrderIn(List<Order> orderIds);

}
