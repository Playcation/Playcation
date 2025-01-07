package com.example.playcation.user.service;

import com.example.playcation.enums.Auth;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.InvalidInputException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.s3.service.S3Service;
import com.example.playcation.user.dto.DeletedUserRequestDto;
import com.example.playcation.user.dto.LoginUserRequestDto;
import com.example.playcation.user.dto.LoginUserResponseDto;
import com.example.playcation.user.dto.SignInUserRequestDto;
import com.example.playcation.user.dto.UpdatedUserPasswordRequestDto;
import com.example.playcation.user.dto.UpdatedUserRequestDto;
import com.example.playcation.user.dto.UserResponseDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JwtTokenProvider;
import com.example.playcation.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final S3Service s3Service;

  public LoginUserResponseDto login(LoginUserRequestDto loginUserRequestDto) {
    User user = userRepository.findByEmailOrElseThrow(loginUserRequestDto.getEmail());
    checkPassword(user, loginUserRequestDto.getPassword());

    String token = jwtTokenProvider.createToken(user.getId(), user.getAuth(), 60*60*600L);
    return LoginUserResponseDto.toDto(user, token);
  }

  public void delete(Long id, DeletedUserRequestDto deletedUserRequestDto) {
    User user = userRepository.findByIdOrElseThrow(id);
    checkPassword(user, deletedUserRequestDto.getPassword());
    user.delete();
    userRepository.save(user);
  }

  public void checkPassword(User user, String password) {
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new InvalidInputException(UserErrorCode.WRONG_PASSWORD);
    }
  }

  public UserResponseDto signUp(SignInUserRequestDto signInUserRequestDto, MultipartFile file) {
    if(userRepository.existsByEmail(signInUserRequestDto.getEmail())){
      throw new DuplicatedException(UserErrorCode.EMAIL_EXIST);
    }
    String filePath = s3Service.uploadFile(file);
    User user = userRepository.save( User.builder()
        .email(signInUserRequestDto.getEmail())
        .password(signInUserRequestDto.getPassword())
        .imageUrl(filePath)
        .name(signInUserRequestDto.getName())
        .auth(Auth.USER)
        .build()
    );
    return UserResponseDto.toDto(user);
  }

  public UserResponseDto findUser(Long id) {
    return UserResponseDto.toDto(userRepository.findByIdOrElseThrow(id));
  }

  public UserResponseDto updateUser(Long id, UpdatedUserRequestDto updatedUserRequestDto, MultipartFile file) {
    User user = userRepository.findByIdOrElseThrow(id);
    checkPassword(user, updatedUserRequestDto.getPassword());
    String filePath = "";
    if(!file.isEmpty()) {
      filePath = s3Service.uploadFile(file);
    }
    user.update(updatedUserRequestDto.getName(), updatedUserRequestDto.getDescription(), filePath);
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }

  public UserResponseDto updateUserPassword(Long id, UpdatedUserPasswordRequestDto updatedUserPasswordRequestDto) {
    User user = userRepository.findByIdOrElseThrow(id);
    checkPassword(user, updatedUserPasswordRequestDto.getOldPassword());
    user.updatePassword(updatedUserPasswordRequestDto.getNewPassword());
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }

  public UserResponseDto updateUserAuth(Long id) {
    User user = userRepository.findByIdOrElseThrow(id);
    if(user.getAuth() != Auth.USER){
      throw new NoAuthorizedException(UserErrorCode.NOT_AUTHORIZED_MANAGER);
    }
    user.updateAuth();
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }
}
