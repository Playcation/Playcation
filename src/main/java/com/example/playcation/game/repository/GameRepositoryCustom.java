package com.example.playcation.game.repository;

import com.example.playcation.game.dto.PagingGameResponseDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

public interface GameRepositoryCustom {

  PagingGameResponseDto searchGames(Pageable pageable, String title, Long categoryId,
      BigDecimal price, LocalDate createdAt);
}
