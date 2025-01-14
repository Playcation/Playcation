package com.example.playcation.order.controller;

import com.example.playcation.order.service.OrderManagerService;
import com.example.playcation.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/orders")
public class OrderManagerController {

  private final OrderManagerService orderService;
  private final JWTUtil jwtUtil;

}
