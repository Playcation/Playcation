package com.example.playcation.game.repository;

import com.example.playcation.game.dto.PagingGameResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface GameRepositoryCustom {

  PagingGameResponseDto searchGames(Pageable pageable, String title,
      String category,
      BigDecimal price, LocalDateTime createdAt);
}
