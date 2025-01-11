package com.example.playcation.user.service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.service.S3Service;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UpdatedUserPasswordRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.user.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class UserServiceIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private S3Service s3Service;

  private User user;
  private FileDetail fileDetail;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .id(1L)
        .email("test@example.com")
        .password("encodedPassword")
        .name("Test User")
        .role(Role.USER)
        .social(Social.NORMAL)
        .imageUrl("test-image-url")
        .build();

    fileDetail = new FileDetail("Game", "test-file-path", "test-file-name", "test-file-path", 10000L, "image/png");
  }


  @Test
  void checkPassword() {
  }

  @Test
  void signUp() {
  }

  @Test
  void findUser() {
  }

  @Test
  void updateUser() {
  }

  @Test
  void updateUserPassword() {
  }

  @Test
  void updateUserAuth() {
  }

  @Test
  void uploadFiles() {
  }


  @Test
  void signUp_ShouldCreateUser_WhenValidRequest() {
    SignInUserRequestDto requestDto = new SignInUserRequestDto("test@example.com", "password", "Test User");
    MultipartFile file = mock(MultipartFile.class);

//    when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
//    when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
//    when(s3Service.uploadFile(file)).thenReturn(fileDetail);
//    when(userRepository.save(any(User.class))).thenReturn(user);

    UserResponseDto response = userService.signUp(requestDto, file);

    assertThat(response.getEmail()).isEqualTo(user.getEmail());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void signUp_ShouldThrowException_WhenEmailAlreadyExists() {
    SignInUserRequestDto requestDto = new SignInUserRequestDto("test@example.com", "password", "Test User");
    when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

    assertThrows(DuplicatedException.class, () -> userService.signUp(requestDto, mock(MultipartFile.class)));
  }

  @Test
  void updateUserPassword_ShouldUpdatePassword_WhenOldPasswordMatches() {
    UpdatedUserPasswordRequestDto requestDto = new UpdatedUserPasswordRequestDto("oldPassword", "newPassword");
    when(userRepository.findByIdOrElseThrow(user.getId())).thenReturn(user);
    when(passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())).thenReturn(true);
    when(passwordEncoder.encode(requestDto.getNewPassword())).thenReturn("newEncodedPassword");

    UserResponseDto response = userService.updateUserPassword(user.getId(), requestDto);

    assertThat(response).isNotNull();
    verify(userRepository, times(1)).findByIdOrElseThrow(user.getId());
  }

  @Test
  void updateUserPassword_ShouldThrowException_WhenOldPasswordDoesNotMatch() {
    UpdatedUserPasswordRequestDto requestDto = new UpdatedUserPasswordRequestDto("wrongPassword", "newPassword");
    when(userRepository.findByIdOrElseThrow(user.getId())).thenReturn(user);
    when(passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())).thenReturn(false);

    assertThrows(
        InvalidInputException.class, () -> userService.updateUserPassword(user.getId(), requestDto));
  }
}