package com.example.playcation.oauth2.dto;

import com.example.playcation.enums.Social;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleResponseDto implements BasicOAuth2Dto{

  private final Map<String, Object> attribute;

  @Override
  public String getProvider() {

    return Social.GOOGLE.name();
  }

  @Override
  public String getProviderId() {

    return attribute.get("sub").toString();
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
