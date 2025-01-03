package com.example.playcation.user.service;

import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JwtTokenProvider;
import com.example.playcation.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public UserResponseDto login(String email, String password) {
    User user = userRepository.findByEmailOrElseThrow(email);

    // 비밀번호 확인
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new InvalidInputException(UserErrorCode.WRONG_PASSWORD);
    }

    return UserResponseDto.toDto(user);
  }

  public void delete(String token, String password) {
  }
}