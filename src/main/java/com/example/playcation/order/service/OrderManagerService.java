package com.example.playcation.order.service;


import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderManagerService {

  private final OrderDetailRepository orderDetailRepository;
  private final GameRepository gameRepository;

  /**
   * 게임 id의 주문 내역을 모두 반환 + 페이징
   *
   * @param userId 현재 로그인한 유저 식별자
   * @param gameId 검색하려는 게임 id
   * @apiNote 검색하려는 게임이 로그인한 유저의 소유가 아닐 경우 예외.
   */
  public Page<OrderDetail> findGameOrderDetails(int page, int size, Long userId, Long gameId) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "id"));

    if (!gameRepository.existsByIdAndUserId(gameId, userId)) {
      throw new InvalidInputException(GameErrorCode.DOES_NOT_MATCH);
    }
    return orderDetailRepository.findAllByGameId(gameId, pageable);
  }
}
