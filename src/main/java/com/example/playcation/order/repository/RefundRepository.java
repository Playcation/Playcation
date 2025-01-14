package com.example.playcation.order.repository;


import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.entity.Refund;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {

  List<Refund> findAllByOrderDetailIn(List<OrderDetail> details);
}
