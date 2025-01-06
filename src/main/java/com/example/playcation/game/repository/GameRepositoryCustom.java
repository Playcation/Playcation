package com.example.playcation.game.repository;

import com.example.playcation.game.Dto.PageGameResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;

public interface GameRepositoryCustom {
  PageGameResponseDto searchGames(PageRequest pageRequest, String title, String category, BigDecimal price, LocalDateTime createdAt);
}
