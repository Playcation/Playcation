package com.example.playcation.oauth2.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.playcation.config.EmbeddedRedisConfig;
import com.example.playcation.config.EmbeddedRedissonConfig;
import com.example.playcation.config.RedissonConfig;
import jakarta.servlet.ServletException;
import java.util.List;
import com.example.playcation.common.TokenSettings;
import com.example.playcation.enums.Role;
import com.example.playcation.oauth2.dto.OAuth2UserDto;
import com.example.playcation.oauth2.dto.UserDto;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Transactional
@SpringBootTest(classes = {
    EmbeddedRedisConfig.class,
    EmbeddedRedissonConfig.class
}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
//@Import({EmbeddedRedisConfig.class, EmbeddedRedissonConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SuccessHandlerTest {

  @Autowired
  private SuccessHandler successHandler;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private JWTUtil jwtUtil;

  private Authentication authentication;

  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  void setUp(){
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();

    User user = User.builder()
        .email("test@example.com")
        .name("name")
        .password("password")
        .role(Role.USER)
        .build();
    userRepository.save(user);
    OAuth2UserDto userDetails = new OAuth2UserDto(new UserDto(user.getEmail(), user.getName(), user.getRole()));
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", user.getEmail());

    authentication = new OAuth2AuthenticationToken(userDetails,
        List.of(new SimpleGrantedAuthority("ROLE_USER")), "google");
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  @DisplayName("소셜 로그인 성공")
  void onAuthenticationSuccess() throws IOException, ServletException {
    // Given
    // When
    successHandler.onAuthenticationSuccess(request, response, authentication);
    // Then
    assertThat(response.getHeader("Set-Cookie")).isNotNull();
    assertThat(response.getHeader("Location")).isNotNull();
    String storedToken = redisTemplate.opsForValue().get(TokenSettings.REFRESH_TOKEN_CATEGORY + "1");
    System.out.println("Redis Keys: " + redisTemplate.keys("*"));
    assertThat(storedToken).isNotNull();
  }
}
