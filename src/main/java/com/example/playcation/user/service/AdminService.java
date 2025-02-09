package com.example.playcation.user.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.game.service.GameService;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.entity.UserFile;
import com.example.playcation.user.dto.AdminRequestDto;
import com.example.playcation.user.dto.RegistManagerResponseDto;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.RegistManager;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.RegistManagerRepository;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final RegistManagerRepository registManagerRepository;
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
    userRepository.save(user);
    registManagerRepository.deleteByUserId(user.getId());
    return UserResponseDto.toDto(user);
  }

  public boolean existsAdminUser() {
    return userRepository.existsByRole(Role.ADMIN);
  }

  public PagingDto<RegistManagerResponseDto> findRegistManagerUsers(Pageable pageable) {
    Page<RegistManager> userList = registManagerRepository.findAll(pageable);
    List<RegistManagerResponseDto> users = userList.stream().map(RegistManagerResponseDto::toDto).toList();
    Long count = userList.getTotalElements();
    return new PagingDto<>(users, count);
  }
}
