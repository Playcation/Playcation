package com.example.playcation.config;

import com.example.playcation.filter.JwtAuthenticationFilter;
import com.example.playcation.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;

  public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    return new JwtAuthenticationFilter(jwtTokenProvider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    //csrf disable
    http
        .csrf((auth) -> auth.disable());

    //From 로그인 방식 disable
    http
        .formLogin((auth) -> auth.disable());

    //http basic 인증 방식 disable
    http
        .httpBasic((auth) -> auth.disable());

    // JWT 인증 필터 추가
    http.addFilterAt(jwtAuthenticationFilter(jwtTokenProvider), ExceptionTranslationFilter.class);

    //인가 설정
    http.authorizeHttpRequests((auth) -> auth
        .requestMatchers("/users/sign-in","/users/login").permitAll() //인증 없이 허용
        .requestMatchers("/admin").hasRole("ADMIN")             // ADMIN 권한 필요
        .requestMatchers("/admin/**").hasRole("ADMIN")             // ADMIN 권한 필요
        .anyRequest().authenticated());                          // 나머지 인증 필요

    //세션 설정
    http.sessionManagement((session) -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
