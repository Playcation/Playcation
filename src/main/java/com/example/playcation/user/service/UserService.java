package com.example.playcation.user.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.Grade;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.NotFoundException;
import com.example.playcation.exception.PaymentErrorCode;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.entity.UserFile;
import com.example.playcation.s3.repository.FileDetailRepository;
import com.example.playcation.s3.repository.UserFileRepository;
import com.example.playcation.s3.service.S3Service;
import com.example.playcation.user.dto.DeletedUserRequestDto;
import com.example.playcation.user.dto.RegistManagerRequestDto;
import com.example.playcation.user.dto.RegistManagerResponseDto;
import com.example.playcation.user.dto.RestoreUserRequestDto;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UpdatedUserPasswordRequestDto;
import com.example.playcation.user.dto.UpdatedUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.Point;
import com.example.playcation.user.entity.RegistManager;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.PointRepository;
import com.example.playcation.user.repository.RegistManagerRepository;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserFileRepository userFileRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final PointRepository pointRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final S3Service s3Service;
  private final FileDetailRepository fileDetailRepository;
  private final RegistManagerRepository registManagerRepository;

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
        .username(signInUserRequestDto.getUsername())
        .role(Role.USER)
        .social(Social.NORMAL)
        .grade(Grade.NORMAL)
        .build()
    );
    userFileRepository.save(new UserFile(user, fileDetail));
    pointRepository.save(new Point(user));
    return UserResponseDto.toDto(user);
  }

  // 유저 조회
  public UserResponseDto findUser(Long id) {
    return UserResponseDto.toDto(userRepository.findByIdOrElseThrow(id));
  }

  // 유저 검색
  public PagingDto<UserResponseDto> searchUser(String username, Pageable pageable) {
    Page<User> userList = userRepository.findAllByUsername(username, pageable);
    List<UserResponseDto> users = userList.stream().map(UserResponseDto::toDto).toList();
    Long count = userList.getTotalElements();
    return new PagingDto<>(users, count);
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

    user.update(updatedUserRequestDto.getUsername(),
        updatedUserRequestDto.getDescription(),
        fileDetail);
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }

  // 사진 변경
  private FileDetail updateFileDetail(User user, MultipartFile file) {
    if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
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
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }

  // 회원 삭제
  @Transactional
  public void delete(Long id, DeletedUserRequestDto deletedUserRequestDto) {
    User user = userRepository.findByIdOrElseThrow(id);
    // S3에서 파일 삭제
    if (user.getImageUrl() != null) {
      if(!user.getImageUrl().isEmpty()) {
        userFileRepository.deleteByUserId(id);
        s3Service.deleteFile(user.getImageUrl());
      }
    }

    checkPassword(user, deletedUserRequestDto.getPassword());
    user.delete();
    userRepository.save(user);
  }


  /**
   * 탈퇴일로부터 30일 지난 유저 영구 삭제
   *
   * @apiNote {@link com.example.playcation.scheduler.CommonScheduler} 매 정각 스케줄링
   */
  @Transactional
  public void deleteExpiredUsers(Long waitTime) {

    List<User> expiredUsers = userRepository.findAllByDeletedAtIsBeforeAndNameIsNotNull(
        LocalDateTime.now().minusDays(waitTime));

    for (User u : expiredUsers) {
      try {
        s3Service.deleteFile(u.getImageUrl());
      } catch (NotFoundException e) {
        log.info("{}: {}", u.getName(), e.getMessage());
      }
      u.expire();
    }

    userRepository.saveAll(expiredUsers);
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

  @Transactional
  public String attendanceUser(Long id) {
    User user = userRepository.findByIdOrElseThrow(id);
    Point pointDetail = pointRepository.getPointByUserIdOrElseThrow(id);
    String redisKey = "attendance" + user.getId();
    BigDecimal point;
    if(redisTemplate.opsForValue().get(redisKey) == null) {
      ValueOperations<String, String> ops = redisTemplate.opsForValue();
      Duration seconds = calculateSeconds();
      ops.set(redisKey, String.valueOf(true), seconds);

      point = pointDetail.getFreePoint(user);
      pointRepository.save(pointDetail);
      return "현재 포인트는" + point.toString() + "입니다.";
    }else{
      throw new DuplicatedException(PaymentErrorCode.ALREADY_GET_FREE_POINT);
    }
  }

  private Duration calculateSeconds(){
    // 현재 시간
    LocalDateTime now = LocalDateTime.now();

    // 오늘 자정
    LocalDateTime midnight = now.toLocalDate().atTime(LocalTime.MAX).plusSeconds(1);

    // 자정까지 남은 시간 계산
    Duration duration = Duration.between(now, midnight);
    long secondsUntilMidnight = duration.getSeconds();

    return Duration.ofSeconds(secondsUntilMidnight);
  }

  public String registerManager(Long id, @Valid RegistManagerRequestDto registManagerRequestDto, MultipartFile file) {
    User user = userRepository.findByIdOrElseThrow(id);
    FileDetail image = null;
    if(file != null && file.isEmpty()){
      image = s3Service.uploadFile(file);
    }
    RegistManager registManager = RegistManager.builder()
        .user(user)
        .title(registManagerRequestDto.getTitle())
        .description(registManagerRequestDto.getDescription())
        .price(registManagerRequestDto.getPrice())
        .mainPicture(image != null ? image.getFilePath() : "")
        .termsAgreement(registManagerRequestDto.getTermsAgreement())
        .build();
    registManagerRepository.save(registManager);
    return "성공";
  }
}
