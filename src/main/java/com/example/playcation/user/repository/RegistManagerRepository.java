package com.example.playcation.user.repository;

import com.example.playcation.user.entity.RegistManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistManagerRepository extends JpaRepository<RegistManager, Long> {

  void deleteByUserId(Long id);
}
