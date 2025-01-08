package com.example.playcation.user.service;

import com.example.playcation.user.entity.CustomUserDetails;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmailOrElseThrow(username);
    if (user != null) {
      return new CustomUserDetails(user);
    }
    return null;
  }
}
