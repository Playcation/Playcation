package com.example.playcation.oauth2.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.playcation.common.TokenSettings;
import com.example.playcation.enums.Role;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class SuccessHandlerTest {

  @Autowired
  private SuccessHandler successHandler;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private JWTUtil jwtUtil;

  @MockitoBean
  private Authentication authentication;

  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  void setUp(){
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
//  @WithMockUser(username = "test@example.com", roles = "USER")
  void onAuthenticationSuccess() throws IOException {
    // Given
    User user = User.builder()
        .email("test@example.com")
        .name("name")
        .password("password")
        .role(Role.USER)
        .build();
    userRepository.save(user);
    // When
    successHandler.onAuthenticationSuccess(request, response, authentication);
    // Then
    assertThat(response.getHeader(TokenSettings.ACCESS_TOKEN_CATEGORY)).isNotNull();
    assertThat(((MockHttpServletResponse) response).getCookies()).isNotEmpty();
    String storedToken = redisTemplate.opsForValue().get("1");
    assertThat(storedToken).isNotNull();
  }
}