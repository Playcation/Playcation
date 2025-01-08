package com.example.playcation.gametag.repository;

import com.example.playcation.exception.GameTagErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.gametag.entity.GameTag;
import com.example.playcation.tag.entity.Tag;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameTagRepository extends JpaRepository<GameTag, Long>, GameTagRepositoryCustom {
  default GameTag findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new NotFoundException(GameTagErrorCode.GAME_TAG_NOT_FOUND));
  }


  List<GameTag> findGameTagsByGameId(Long game_id);
}
