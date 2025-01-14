package com.example.playcation.user.service;

import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.game.service.GameService;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.entity.UserFile;
import com.example.playcation.user.dto.AdminRequestDto;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final GameService gameService;
  private final String ADMIN_ID = "admin";
  private final String ADMIN_PW = "admin";

  // ADMIN 생성
  @Transactional
  public UserResponseDto signUp() {
    if(userRepository.existsByEmail(ADMIN_ID)){
      throw new DuplicatedException(UserErrorCode.EXIST_ADMIN);
    }
    String password = bCryptPasswordEncoder.encode(ADMIN_PW);
    User admin = User.builder()
        .email(ADMIN_ID)
        .password(password)
        .name(ADMIN_ID)
        .role(Role.ADMIN)
        .build();
    userRepository.save(admin);
    return UserResponseDto.toDto(admin);
  }

  // MANAGER 권한 수정
  @Transactional
  public UserResponseDto updateUserAuth(Long id) {
    User user = userRepository.findByIdOrElseThrow(id);
    if(user.getRole() != Role.USER){
      throw new NoAuthorizedException(UserErrorCode.NOT_AUTHORIZED_MANAGER);
    }
    user.updateRole();
    return UserResponseDto.toDto(user);
  }

  public boolean existsAdminUser() {
    return userRepository.existsByRole(Role.ADMIN);
  }
}
