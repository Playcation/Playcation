package com.example.playcation.user.service.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.entity.UserFile;
import com.example.playcation.s3.repository.FileDetailRepository;
import com.example.playcation.s3.repository.UserFileRepository;
import com.example.playcation.s3.service.S3Service;
import com.example.playcation.user.dto.DeletedUserRequestDto;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UpdatedUserPasswordRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private S3Service s3Service;

  @Mock
  private UserFileRepository userFileRepository;

  private User user;

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
  }

  @Test
  @DisplayName("회원삭제_비밀번호가 일치할 때")
  void deleteUser_WhenPasswordMatches() {
    // Given
    DeletedUserRequestDto requestDto = new DeletedUserRequestDto("password");
    when(userRepository.findByIdOrElseThrow(user.getId())).thenReturn(user);
    when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);
    doNothing().when(userFileRepository).deleteByUserId(any());
    doNothing().when(s3Service).deleteFile(any());
    // When
    userService.delete(user.getId(), requestDto);
    // Then
    assertThat(user.getDeletedAt()).isNotNull();
  }

  @Test
  @DisplayName("회원삭제_비밀번호가 잘못 되었을 때")
  void deleteUser_WhenPasswordDoesNotMatch() {
    // Given
    DeletedUserRequestDto requestDto = new DeletedUserRequestDto("wrongPassword");
    when(userRepository.findByIdOrElseThrow(user.getId())).thenReturn(user);
    when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(false);
    doNothing().when(userFileRepository).deleteByUserId(any());
    doNothing().when(s3Service).deleteFile(any());
    // When
    // Then
    assertThrows(InvalidInputException.class, () -> userService.delete(user.getId(), requestDto));
  }
}
