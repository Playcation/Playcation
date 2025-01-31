package com.example.playcation.oauth2.service;

import com.example.playcation.enums.Grade;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.oauth2.dto.BasicOAuth2Dto;
import com.example.playcation.oauth2.dto.GoogleResponseDto;
import com.example.playcation.oauth2.dto.KakaoResponseDto;
import com.example.playcation.oauth2.dto.NaverResponseDto;
import com.example.playcation.oauth2.dto.OAuth2UserDto;
import com.example.playcation.oauth2.dto.UserDto;
import com.example.playcation.user.entity.Point;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.PointRepository;
import com.example.playcation.user.repository.UserRepository;
import io.lettuce.core.StrAlgoArgs.By;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService {

  private final UserRepository userRepository;
  private final PointRepository pointRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

    // 소셜 로그인 응답 객체 가져오기
    BasicOAuth2Dto oAuth2Response = getOAuth2Response(registrationId, oAuth2User);
    if (oAuth2Response == null) return null;

    String email = oAuth2Response.getEmail();
    User existData = userRepository.findByEmail(email).orElse(null);

    return (existData == null) ? createNewSocialUser(oAuth2Response, registrationId) : updateExistingUser(existData, registrationId);
  }

  // 소셜 로그인 응답 객체 생성 메서드 (Google, Naver 구분)
  private BasicOAuth2Dto getOAuth2Response(String registrationId, OAuth2User oAuth2User) {
    if ("NAVER".equals(registrationId)) {
      return new NaverResponseDto(oAuth2User.getAttributes());
    } else if ("GOOGLE".equals(registrationId)) {
      return new GoogleResponseDto(oAuth2User.getAttributes());
    } else if("KAKAO".equals(registrationId)){
      return new KakaoResponseDto(oAuth2User.getAttributes());
    }
    return null;
  }

  // 새로운 소셜 로그인 유저 생성 메서드
  private OAuth2User createNewSocialUser(BasicOAuth2Dto oAuth2Response, String registrationId) {
    User user = User.builder()
        .email(oAuth2Response.getEmail())
        .name(oAuth2Response.getName())
        .username(oAuth2Response.getName())
        .role(Role.USER)
        .social(Social.valueOf(registrationId))
        .password(bCryptPasswordEncoder.encode(oAuth2Response.getPassword()))
        .grade(Grade.NORMAL)
        .build();

    userRepository.save(user);
    pointRepository.save(new Point(user));
    return new OAuth2UserDto(new UserDto(user.getEmail(), user.getName(), Role.USER));
  }

  // 기존 유저 업데이트 메서드 (소셜 플랫폼 갱신)
  private OAuth2User updateExistingUser(User existData, String registrationId) {
    if(existData.getDeletedAt() != null){
      throw new DuplicatedException(UserErrorCode.EMAIL_EXIST);
    }
    if(!existData.getSocial().name().equals(registrationId)){
      if(!existData.getSocial().equals(Social.NORMAL)) {
        throw new NoAuthorizedException(UserErrorCode.EXIST_SOCIAL);
      }
      existData.updateSocial(Social.valueOf(registrationId.toUpperCase()));
      userRepository.save(existData);
    }

    return new OAuth2UserDto(new UserDto(existData.getEmail(), existData.getName(), existData.getRole()));
  }
}
