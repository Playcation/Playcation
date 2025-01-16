package com.example.playcation.user.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.entity.UserFile;
import com.example.playcation.s3.repository.FileDetailRepository;
import com.example.playcation.s3.repository.UserFileRepository;
import com.example.playcation.s3.service.S3Service;
import com.example.playcation.user.dto.DeletedUserRequestDto;
import com.example.playcation.user.dto.RestoreUserRequestDto;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UpdatedUserPasswordRequestDto;
import com.example.playcation.user.dto.UpdatedUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb.PageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserFileRepository userFileRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final S3Service s3Service;
  private final FileDetailRepository fileDetailRepository;

  // 회원 가입
  @Transactional
  public UserResponseDto signUp(SignInUserRequestDto signInUserRequestDto, MultipartFile file) {
    if (userRepository.existsByEmail(signInUserRequestDto.getEmail())) {
      throw new DuplicatedException(UserErrorCode.EMAIL_EXIST);
    }
    String password = bCryptPasswordEncoder.encode(signInUserRequestDto.getPassword());
    FileDetail fileDetail = null;
    if (file != null && !file.getName().isEmpty()) {
      fileDetail = s3Service.uploadFile(file);
    }
    User user = userRepository.save(User.builder()
        .email(signInUserRequestDto.getEmail())
        .password(password)
        .imageUrl(fileDetail == null ? "" : fileDetail.getFilePath())
        .name(signInUserRequestDto.getName())
        .role(Role.USER)
        .social(Social.NORMAL)
        .build()
    );
    userFileRepository.save(new UserFile(user, fileDetail));
    return UserResponseDto.toDto(user);
  }

  // 유저 조회
  public UserResponseDto findUser(Long id) {
    return UserResponseDto.toDto(userRepository.findByIdOrElseThrow(id));
  }

  // 유저 정보 수정
  @Transactional
  public UserResponseDto updateUser(Long id, UpdatedUserRequestDto updatedUserRequestDto,
      MultipartFile file) {
    User user = userRepository.findByIdOrElseThrow(id);
    checkPassword(user, updatedUserRequestDto.getPassword());
    FileDetail fileDetail = null;

    // 사진 변경
    if (file != null) {
      fileDetail = updateFileDetail(user, file);
    }

    user.update(updatedUserRequestDto.getName(),
        updatedUserRequestDto.getDescription(),
        fileDetail);
    return UserResponseDto.toDto(user);
  }

  // 사진 변경
  private FileDetail updateFileDetail(User user, MultipartFile file) {
    if (!user.getImageUrl().isEmpty()) {
      FileDetail fileDetail = fileDetailRepository.findByFilePathOrElseThrow(user.getImageUrl());
      userFileRepository.deleteByUserIdAndFileDetailId(user.getId(), fileDetail.getId());
      s3Service.deleteFile(user.getImageUrl());
    }
    if (!file.getOriginalFilename().isEmpty()) {
      FileDetail uploadFileDetail = s3Service.uploadFile(file);
      userFileRepository.save(new UserFile(user, uploadFileDetail));
      return uploadFileDetail;
    }
    return new FileDetail();
  }

  // 비밀번호 변경
  @Transactional
  public UserResponseDto updateUserPassword(Long id,
      UpdatedUserPasswordRequestDto updatedUserPasswordRequestDto) {
    User user = userRepository.findByIdOrElseThrow(id);
    checkPassword(user, updatedUserPasswordRequestDto.getOldPassword());
    user.updatePassword(
        bCryptPasswordEncoder.encode(updatedUserPasswordRequestDto.getNewPassword()));
    return UserResponseDto.toDto(user);
  }

  // 회원 삭제
  @Transactional
  public void delete(Long id, DeletedUserRequestDto deletedUserRequestDto) {
    User user = userRepository.findByIdOrElseThrow(id);
    // S3에서 파일 삭제
    if (!user.getImageUrl().isEmpty()) {
      userFileRepository.deleteByUserId(id);
      s3Service.deleteFile(user.getImageUrl());
    }

    checkPassword(user, deletedUserRequestDto.getPassword());
    user.delete();
  }

  // 비밀번호 확인
  @Transactional
  public void checkPassword(User user, String password) {
    if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
      throw new InvalidInputException(UserErrorCode.WRONG_PASSWORD);
    }
  }

  // 임시 실행 확인용
  @Transactional
  public UserResponseDto uploadFiles(Long id, List<MultipartFile> files) {
    User user = userRepository.findByIdOrElseThrow(id);
    List<FileDetail> urls = s3Service.uploadFiles(files).join();
    urls.forEach(url -> {
      userFileRepository.save(new UserFile(user, url));
    });
    return UserResponseDto.toDto(user);
  }

  public PagingDto<UserResponseDto> findAllUsers(Pageable pageable) {
    Page<User> userList = userRepository.findAll(pageable);
    return new PagingDto<>(userList.stream().map(UserResponseDto::toDto).toList(),
        userList.getTotalElements());
  }

  public UserResponseDto restoreUser(@Valid RestoreUserRequestDto requestDto) {
    return null;
  }

  public User authenticateUser(SignInUserRequestDto signInUserRequestDto) {
    User user = userRepository.findByEmail(signInUserRequestDto.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

    if (!bCryptPasswordEncoder.matches(signInUserRequestDto.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid email or password");
    }

    return user;
  }
}
