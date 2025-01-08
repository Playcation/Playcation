package com.example.playcation.oauth2.dto;

import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NaverResponseDto implements BasicOAuth2Dto{

  private final Map<String, Object> attribute;

  @Override
  public String getProvider() {
    return "naver";
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
}
