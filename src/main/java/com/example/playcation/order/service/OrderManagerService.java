package com.example.playcation.order.service;


import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.order.dto.OrderDetailResponseDto;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.repository.OrderDetailRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderManagerService {

  private final OrderDetailRepository orderDetailRepository;
  private final GameRepository gameRepository;

  /**
   * 게임 id의 판매 내역을 모두 반환
   *
   * @param userId 현재 로그인한 유저 식별자
   * @param gameId 검색하려는 게임 id
   * @apiNote 검색하려는 게임이 로그인한 유저의 소유가 아닐 경우 예외.
   */
  public List<OrderDetailResponseDto> findGameOrderDetails(Long userId, Long gameId) {

    if (!gameRepository.existsByIdAndUserId(gameId, userId)) {
      throw new InvalidInputException(GameErrorCode.DOES_NOT_MATCH);
    }
    List<OrderDetail> details = orderDetailRepository.findAllByGameId(gameId);

    return details.stream().map(OrderDetailResponseDto::toDto).toList();
  }
}
