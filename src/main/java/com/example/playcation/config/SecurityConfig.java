package com.example.playcation.config;

import com.example.playcation.filter.JWTFilter;
import com.example.playcation.filter.LoginFilter;
import com.example.playcation.filter.CustomLogoutFilter;
import com.example.playcation.token.repository.TokenRepository;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final TokenRepository tokenRepository;
  private final JWTUtil jwtUtil;


  public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, TokenRepository tokenRepository) {
    this.authenticationConfiguration = authenticationConfiguration;
    this.tokenRepository = tokenRepository;
    this.jwtUtil = jwtUtil;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository,
      TokenRepository tokenRepository) throws Exception {

    //csrf disable
    http
        .csrf((auth) -> auth.disable());

    //From 로그인 방식 disable
    http
        .formLogin((auth) -> auth.disable());

    //http basic 인증 방식 disable
    http
        .httpBasic((auth) -> auth.disable());

    http
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/", "/users/sign-in", "/login", "/users/refresh").permitAll()
            .requestMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated());

    http
        .addFilterBefore(new JWTFilter(userRepository, jwtUtil), LoginFilter.class);
    http
        .addFilterBefore(new CustomLogoutFilter(jwtUtil, tokenRepository), LogoutFilter.class);
    http
        .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, tokenRepository), UsernamePasswordAuthenticationFilter.class);

    //세션 설정
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
