package com.example.playcation.user.repository;

import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
  default User findByEmailOrElseThrow(String email) {
    return findByEmail(email).orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));
  }

}
