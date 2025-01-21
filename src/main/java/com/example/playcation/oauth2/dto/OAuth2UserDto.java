package com.example.playcation.oauth2.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class OAuth2UserDto implements OAuth2User {

  private final UserDto userDto;

  @Override
  public Map<String, Object> getAttributes() {
    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    Collection<GrantedAuthority> collection = new ArrayList<>();

    collection.add(new GrantedAuthority() {

      @Override
      public String getAuthority() {

        return userDto.getRole().toString();
      }
    });

    return collection;
  }

  @Override
  public String getName() {
    return userDto.getName();
  }

  public String getEmail() {
    return userDto.getEmail();
  }

  public String getPassword(){
    return userDto.getPassword();
  }
}
