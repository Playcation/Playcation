package com.example.playcation.order.service;


import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.order.repository.OrderDetailRepository;
import com.example.playcation.order.repository.OrderRepository;
import com.example.playcation.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderManagerService {

  private final OrderRepository orderRepository;
  private final OrderDetailRepository orderDetailRepository;
  private final UserRepository userRepository;
  private final GameRepository gameRepository;

}
