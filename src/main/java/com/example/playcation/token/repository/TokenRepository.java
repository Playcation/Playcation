package com.example.playcation.token.repository;

import com.example.playcation.token.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

  Boolean existsByRefresh(String refresh);

  @Transactional
  void deleteByRefresh(String refresh);

}
