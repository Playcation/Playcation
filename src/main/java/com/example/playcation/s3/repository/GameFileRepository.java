package com.example.playcation.s3.repository;

import com.example.playcation.enums.ImageRole;
import com.example.playcation.exception.FileErrorCode;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.s3.entity.GameFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface GameFileRepository extends JpaRepository<GameFile, Long> {

  default GameFile findByIdOrElseThrow(Long id){
    GameFile gameFile = findById(id).orElseThrow(() -> new NotFoundException(
        FileErrorCode.NOT_FOUND_FILE));
    return gameFile;
  }

  void deleteByGameIdAndFileDetailId(Long game_id, Long fileDetail_id);

  GameFile findByGameId(Long gameId);

  List<GameFile> findGameFileByGameIdAndImageRole(Long gameId, ImageRole imageRole);
}
