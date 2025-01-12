package com.example.playcation.game.repository;

import com.example.playcation.common.PagingDto;
import com.example.playcation.game.dto.CreatedGameResponseDto;
import com.example.playcation.game.dto.PagingGameResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;

public interface GameRepositoryCustom {

  PagingGameResponseDto searchGames(PageRequest pageRequest, String title,
      String category,
      BigDecimal price, LocalDateTime createdAt);
}
