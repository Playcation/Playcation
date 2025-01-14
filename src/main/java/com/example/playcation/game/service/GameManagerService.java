package com.example.playcation.game.service;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.game.dto.ManagerGameDetailResponseDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.repository.OrderDetailRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameManagerService {

  private final GameRepository gameRepository;
  private final OrderDetailRepository orderDetailRepository;

  /**
   * 유저가 현재 판매중인 게임 목록을 반환
   */
  public List<CartGameResponseDto> findSellingGames(Long userId) {

    List<Game> games = gameRepository.findAllByUserId(userId);
    return games.stream().map(CartGameResponseDto::toDto).toList();
  }

  /**
   * 판매중인 게임 중 하나의 상세 정보를 조회
   *
   * @param gameId 상세 정보를 조회할 게임 식별자
   * @return 게임 판매 총량, 판매 내역{@link OrderDetail}
   */
  public ManagerGameDetailResponseDto findSellingGameDetail(Long userId, Long gameId) {

    if (!gameRepository.existsByIdAndUserId(userId, gameId)) {
      throw new InvalidInputException(GameErrorCode.DOES_NOT_MATCH);
    }

    List<OrderDetail> details = orderDetailRepository.findAllByGameId(gameId);
    Long count = orderDetailRepository.countByGameId(gameId);

    return ManagerGameDetailResponseDto.toDto(details, count);
  }
}
