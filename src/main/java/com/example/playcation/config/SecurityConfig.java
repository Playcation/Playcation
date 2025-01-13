package com.example.playcation.config;

import com.example.playcation.filter.JWTFilter;
import com.example.playcation.filter.CustomLoginFilter;
import com.example.playcation.filter.CustomLogoutFilter;
import com.example.playcation.oauth2.handler.SuccessHandler;
import com.example.playcation.oauth2.service.OAuth2Service;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final RedisTemplate<String, String> redisTemplate;
  private final SuccessHandler successHandler;
  private final OAuth2Service oAuth2Service;
  private final JWTUtil jwtUtil;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(
        "/login.html",       // 로그인 페이지
        "/css/**",           // CSS 파일
        "/js/**",            // JavaScript 파일
        "/images/**",        // 이미지 파일
        "/favicon.ico"       // 파비콘
    );
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      UserRepository userRepository) throws Exception {

    http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

          @Override
          public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowCredentials(true);
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
            configuration.setAllowedMethods(Arrays.asList("*"));
            configuration.setAllowedHeaders(Arrays.asList("*"));
            configuration.setMaxAge(3600L);

            configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

            return configuration;
          }
        }));

    // csrf disable
    http.csrf(AbstractHttpConfigurer::disable);
    // form 로그인 방식 disable
    http.formLogin(AbstractHttpConfigurer::disable);
//    http.formLogin(form -> form
//        .loginPage("/login.html")  // 기본 로그인 페이지 경로 지정
//        .permitAll()
//    );
    // http basic 인증 방식 disable
    http.httpBasic(AbstractHttpConfigurer::disable);

    // oauth2
    http.oauth2Login((oauth2) -> oauth2
            .userInfoEndpoint((userInfoEndpointConfig) ->
                userInfoEndpointConfig.userService(oAuth2Service))
            .successHandler(successHandler));

    http.authorizeHttpRequests((auth) -> auth
            .requestMatchers("/", "/users/sign-in", "/auth/login", "/oauth2-login", "/refresh", "/error").permitAll()
            .requestMatchers("/users/\\d/update/role").hasAuthority("ADMIN")
            .requestMatchers("/games").hasAuthority("MANAGER")
            .anyRequest().authenticated()
    );

    http.addFilterBefore(new JWTFilter(userRepository, jwtUtil), CustomLoginFilter.class);
    http.addFilterBefore(new CustomLogoutFilter(jwtUtil), LogoutFilter.class);
    http.addFilterAt(new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

    // 세션 설정
    http.sessionManagement((session) ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    return http.build();
  }
}
