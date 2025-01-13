package com.example.playcation.oauth2.service;

import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.UserErrorCode;
import com.example.playcation.oauth2.dto.BasicOAuth2Dto;
import com.example.playcation.oauth2.dto.GoogleResponseDto;
import com.example.playcation.oauth2.dto.NaverResponseDto;
import com.example.playcation.oauth2.dto.OAuth2UserDto;
import com.example.playcation.oauth2.dto.UserDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    // 소셜 로그인 응답 객체 가져오기
    BasicOAuth2Dto oAuth2Response = getOAuth2Response(registrationId, oAuth2User);
    if (oAuth2Response == null) return null;

    String email = oAuth2Response.getEmail();
    User existData = userRepository.findByEmail(email).orElse(null);

    return (existData == null) ? createNewSocialUser(oAuth2Response, registrationId) : updateExistingUser(existData, registrationId);
  }

  // 소셜 로그인 응답 객체 생성 메서드 (Google, Naver 구분)
  private BasicOAuth2Dto getOAuth2Response(String registrationId, OAuth2User oAuth2User) {
    if ("naver".equals(registrationId)) {
      return new NaverResponseDto(oAuth2User.getAttributes());
    } else if ("google".equals(registrationId)) {
      return new GoogleResponseDto(oAuth2User.getAttributes());
    }
    return null;
  }

  // 새로운 소셜 로그인 유저 생성 메서드
  private OAuth2User createNewSocialUser(BasicOAuth2Dto oAuth2Response, String registrationId) {
    User user = User.builder()
        .email(oAuth2Response.getEmail())
        .password("")
        .name(oAuth2Response.getName())
        .role(Role.USER)
        .social(Social.valueOf(registrationId.toUpperCase()))
        .build();

    userRepository.save(user);
    return new OAuth2UserDto(new UserDto(user.getEmail(), user.getName(), Role.USER));
  }

  // 기존 유저 업데이트 메서드 (소셜 플랫폼 갱신)
  private OAuth2User updateExistingUser(User existData, String registrationId) {
    existData.updateSocial(Social.valueOf(registrationId.toUpperCase()));
    userRepository.save(existData);

    return new OAuth2UserDto(new UserDto(existData.getEmail(), existData.getName(), existData.getRole()));
  }
}
