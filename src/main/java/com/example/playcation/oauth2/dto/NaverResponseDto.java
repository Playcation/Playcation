package com.example.playcation.oauth2.dto;

import com.example.playcation.enums.Social;
import java.util.Map;
import lombok.RequiredArgsConstructor;

public class NaverResponseDto implements BasicOAuth2Dto{

  private final Map<String, Object> attribute;

  public NaverResponseDto(Map<String, Object> attribute) {

    this.attribute = (Map<String, Object>) attribute.get("response");
  }

  @Override
  public String getProvider() {
    return Social.NAVER.name();
  }

  @Override
  public String getProviderId() {
    return attribute.get("id").toString();
  }

  @Override
  public String getEmail() {
    return attribute.get("email").toString();
  }

  @Override
  public String getName() {
    return attribute.get("name").toString();
  }

  @Override
  public String getPassword() {
    return Social.DEFAULT_PASSWORD.getPassword();
  }
}
