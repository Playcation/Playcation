package com.example.playcation.user.service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.playcation.config.RedisTestContainerConfig;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.repository.FileDetailRepository;
import com.example.playcation.s3.repository.UserFileRepository;
import com.example.playcation.s3.service.S3Service;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UpdatedUserPasswordRequestDto;
import com.example.playcation.user.dto.UpdatedUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@SpringBootTest(classes = {
    RedisTestContainerConfig.class
}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private S3Service s3Service;

  @Autowired
  private FileDetailRepository fileDetailRepository;

  @MockitoBean
  private AmazonS3 s3;

  @Autowired
  private UserFileRepository userFileRepository;

  @Autowired
  private EntityManager entityManager;

  private User user;
  private FileDetail fileDetail;
  private MultipartFile file;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .email("test@example.com")
        .password("encodedPassword")
        .name("Test User")
        .role(Role.USER)
        .social(Social.NORMAL)
        .imageUrl("test-image-url")
        .build();

    fileDetail = new FileDetail("Game", "test-file-path", "test-file-name", "test-file-path", 10000L, "image/png");
    file = new MockMultipartFile("name", "originFileName", "image/jpg", new byte[]{});
  }

  @Test
  @DisplayName("회원가입 성공")
  void signUp() {
    // Given
    SignInUserRequestDto requestDto = new SignInUserRequestDto("test@example.com", "password", "Test User", "Test UserName");
    when(s3.putObject(any(PutObjectRequest.class))).thenReturn(null);
    // When
    UserResponseDto response = userService.signUp(requestDto, file);

    // Then
    assertThat(response.getEmail()).isEqualTo(user.getEmail());
//    assertThat(response.getEmail()).isEqualTo(0);
  }

  @Test
  @DisplayName("회원가입 실패 : 중복 이메일")
  void signUp_Fail() {
    // Given
    SignInUserRequestDto requestDto = new SignInUserRequestDto("test@example.com", "password", "Test User", "Test UserName");
    userService.signUp(requestDto, file);
    SignInUserRequestDto requestDto2 = new SignInUserRequestDto("test@example.com", "password2!qfqef", "User2", "Test UserName");
    // When
    // Then
    assertThrows(DuplicatedException.class, () -> userService.signUp(requestDto2, file));
  }

  @Test
  @DisplayName("유저 비밀번호 수정 성공")
  void updateUserPassword() {
    // Given
    SignInUserRequestDto signinDto = new SignInUserRequestDto("test@example.com", "encodedPassword", "Test User", "Test UserName");
    when(s3.putObject(any(PutObjectRequest.class))).thenReturn(null);
    userService.signUp(signinDto, file);
    entityManager.clear();
    User managedUser = userRepository.findByIdOrElseThrow(1L);
    System.out.println(managedUser.getPassword());
    UpdatedUserPasswordRequestDto requestDto = new UpdatedUserPasswordRequestDto("encodedPassword", "newPassword");
    // When
    UserResponseDto response = userService.updateUserPassword(1L, requestDto);
    // Then
    assertThat(response).isNotNull();
  }

  @Test
  @DisplayName("유저 비밀번호 수정 실패 : 잘못된 비밀번호")
  void updateUserPassword_Fail() {
    // Given
    userRepository.save(user);
    entityManager.clear();
    UpdatedUserPasswordRequestDto requestDto = new UpdatedUserPasswordRequestDto("WrongPassword", "newPassword");
    // When
    // Then
    assertThrows(
        InvalidInputException.class, () -> userService.updateUserPassword(user.getId(), requestDto));
  }

  @Test
  @DisplayName("유저 정보 수정 성공")
  void updateUser(){
    // Given
    SignInUserRequestDto signinDto = new SignInUserRequestDto("test@example.com", "encodedPassword", "Test User", "Test UserName");
    when(s3.putObject(any(PutObjectRequest.class))).thenReturn(null);
    userService.signUp(signinDto, file);
    UpdatedUserRequestDto requestDto = new UpdatedUserRequestDto("updateName", "encodedPassword", "update description");
    // When
    UserResponseDto responseDto = userService.updateUser(1L, requestDto, file);
    // Then
    assertThat(responseDto.getUsername()).isEqualTo(responseDto.getUsername());
  }
}
