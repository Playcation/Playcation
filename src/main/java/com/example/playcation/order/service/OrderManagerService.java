package com.example.playcation.order.service;


import static org.springframework.data.domain.Sort.Direction.DESC;

import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.order.dto.PayoutRequestDto;
import com.example.playcation.order.dto.PayoutResponseDto;
import com.example.playcation.order.entity.OrderDetail;
import com.example.playcation.order.entity.PayoutLog;
import com.example.playcation.order.entity.Profit;
import com.example.playcation.order.repository.OrderDetailRepository;
import com.example.playcation.order.repository.PayoutLogRepository;
import com.example.playcation.order.repository.ProfitRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderManagerService {

  private final OrderDetailRepository orderDetailRepository;
  private final GameRepository gameRepository;
  private final ProfitRepository profitRepository;
  private final PayoutLogRepository payoutLogRepository;

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

  /**
   * 수익금 출금 메서드. 출금 내역을 저장하고 기존 수익금 db의 현재 수익금을 변경
   *
   * @param userId 현재 로그인 한 유저 식별자
   * @param dto 요청 출금액
   * @return 요청 출금액, 현재 수익금, 출금 전 수익금
   */
  @Transactional
  public PayoutResponseDto requestPayout(Long userId, PayoutRequestDto dto) {

    // 수익금 정보를 찾아서 현재 수익금 - 출금액
    // todo: 수익금 정보 없을 시 예외처리
    Profit findProfit = profitRepository.findByUserId(userId);
    BigDecimal current = findProfit.getCurrent().subtract(dto.getAmount());

    // 출금 기록 entity 생성
    PayoutLog log = PayoutLog.builder()
        .profit(findProfit)
        .amount(dto.getAmount())
        .current(current)
        .build();

    PayoutLog savedLog = payoutLogRepository.save(log);

    PayoutResponseDto payoutDto = PayoutResponseDto.builder()
        .id(savedLog.getId())
        .amount(savedLog.getAmount())
        .current(savedLog.getCurrent())
        .previous(findProfit.getCurrent())
        .build();

    // 수익금 정보를 현재 상태로 업데이트
    findProfit.updateCurrent(current);

    return payoutDto;
  }
}
