package com.example.playcation.oauth2.dto;

import com.example.playcation.enums.Social;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoResponseDto implements BasicOAuth2Dto {

  private final Map<String, Object> attribute;

  @Override
  public String getProvider() {
    return Social.KAKAO.name();
  }

  @Override
  public String getProviderId() {
    return attribute.get("id").toString();
  }

  @Override
  public String getEmail() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
    return kakaoAccount.get("email").toString();
  }

  @Override
  public String getName() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("properties");
    return kakaoAccount.get("nickname").toString();
  }

  @Override
  public String getPassword() {
    return Social.DEFAULT_PASSWORD.getPassword();
  }
}
