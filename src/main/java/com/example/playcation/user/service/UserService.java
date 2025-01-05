package com.example.playcation.user.service;

import com.example.playcation.common.Auth;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.user.dto.UserLoginResponseDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JwtTokenProvider;
import com.example.playcation.util.PasswordEncoder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public UserLoginResponseDto login(String email, String password) {
    User user = userRepository.findByEmailOrElseThrow(email);
    checkPassword(user, password);

    String token = jwtTokenProvider.createToken(user.getId(), user.getAuth(), 60*60*600L);
    return UserLoginResponseDto.toDto(user, token);
  }

  public void delete(Long id, String password) {
    User user = userRepository.findByIdOrElseThrow(id);
    checkPassword(user, password);
    user.delete();
    userRepository.save(user);
  }

  public void checkPassword(User user, String password) {
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new InvalidInputException(UserErrorCode.WRONG_PASSWORD);
    }
  }

  public UserResponseDto signUp(String email, String password, String name) {
    if(userRepository.existsByEmail(email)){
      throw new DuplicatedException(UserErrorCode.EMAIL_EXIST);
    }
    User user = userRepository.save( new User(
        email,
        password,
        name,
        Auth.USER
    ));
    return UserResponseDto.toDto(user);
  }

  public UserResponseDto findUser(Long id) {
    return UserResponseDto.toDto(userRepository.findByIdOrElseThrow(id));
  }

  public UserResponseDto updateUser(Long id, String password, String name, String description) {
    User user = userRepository.findByIdOrElseThrow(id);
    checkPassword(user, password);
    user.update(name, description);
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }

  public UserResponseDto updateUserPassword(Long id, String oldPassword, String newPassword) {
    User user = userRepository.findByIdOrElseThrow(id);
    checkPassword(user, oldPassword);
    user.updatePassword(newPassword);
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }
}
