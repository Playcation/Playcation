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

    // 어떤 소셜 사이트에서 온 아이디인지 확인하는 로직
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    BasicOAuth2Dto oAuth2Response = null;
    if (registrationId.equals("naver")) {
      oAuth2Response = new NaverResponseDto(oAuth2User.getAttributes());
    }
    else if (registrationId.equals("google")) {
      oAuth2Response = new GoogleResponseDto(oAuth2User.getAttributes());
    }
    else {
      return null;
    }
    String email = oAuth2Response.getEmail();
    
    // 유저가 가입한 유저인지 조회 ( 다른 방법으로 )
    User existData = userRepository.findByEmail(email).orElse(null);

    // 소셜 로그인으로 최초 가입
    if (existData == null) {

      User user = User.builder()
          .email(email)
          .password("")
          .name(oAuth2Response.getName())
          .role(Role.USER)
          .social(Social.valueOf(registrationId.toUpperCase()))
          .build();

      userRepository.save(user);

      UserDto userDTO = new UserDto(email, oAuth2Response.getName(), Role.USER);

      return new OAuth2UserDto(userDTO);
    }
    // 이미 있는 유저에 소셜로그인 연결
    else {

//      existData.setEmail(oAuth2Response.getEmail());
//      existData.setName(oAuth2Response.getName());

      // 제공된 플렛폼으로 소셜을 갱신하준다.
      existData.updateSocial(Social.valueOf(registrationId.toUpperCase()));
      userRepository.save(existData);

      UserDto userDTO = new UserDto(existData.getEmail(), oAuth2Response.getName(), existData.getRole());

      return new OAuth2UserDto(userDTO);
    }
  }
}
