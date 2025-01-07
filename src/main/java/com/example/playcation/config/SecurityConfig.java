package com.example.playcation.config;

import com.example.playcation.filter.JWTFilter;
import com.example.playcation.filter.LoginFilter;
import com.example.playcation.user.service.UserService;
import com.example.playcation.util.JWTUtil;
import com.example.playcation.util.PasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JWTUtil jwtUtil;
  private final ObjectMapper objectMapper;


  public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, ObjectMapper objectMapper) {

    this.authenticationConfiguration = authenticationConfiguration;
    this.jwtUtil = jwtUtil;
    this.objectMapper = objectMapper;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

    return configuration.getAuthenticationManager();
  }

  @Bean
  public LoginFilter loginFilter() throws Exception {
    LoginFilter filter = new LoginFilter(jwtUtil, objectMapper);
    filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
    return filter;
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


    http
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/users/login", "/", "/users/sign-in").permitAll()
            .requestMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated());

    http
        .addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    http
        .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

    //세션 설정
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
