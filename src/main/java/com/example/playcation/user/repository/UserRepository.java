package com.example.playcation.user.repository;

import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);

  default User findByIdOrElseThrow(Long id) {
    User user = findById(id).orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));
    if(user.getDeletedAt() != null) {
      throw new NotFoundException(UserErrorCode.DELETED_USER);
    }
    return user;
  }

  Optional<User> findByEmail(String email);
  default User findByEmailOrElseThrow(String email) {
    User user = findByEmail(email).orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));
    if(user.getDeletedAt() != null) {
      throw new NotFoundException(UserErrorCode.DELETED_USER);
    }
    return user;
  }

}
