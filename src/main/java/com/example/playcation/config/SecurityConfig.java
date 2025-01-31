package com.example.playcation.config;

import com.example.playcation.enums.Role;
import com.example.playcation.filter.JWTFilter;
import com.example.playcation.filter.CustomLoginFilter;
import com.example.playcation.filter.CustomLogoutFilter;
import com.example.playcation.oauth2.handler.SuccessHandler;
import com.example.playcation.oauth2.service.OAuth2Service;
import com.example.playcation.token.service.TokenService;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final ApplicationContext applicationContext;
  private final TokenService tokenService;
  private final SuccessHandler successHandler;
//  private final FailureHandler failureHandler;
  private final JWTUtil jwtUtil;

  private String[] WHITE_LIST = new String[]{
      "/", "/email","/mail-check", "/oauth2/**", "*/sign-in", "/oauth2-login", "/refresh", "/error", "/token/refresh", "/h2-console/**"
  };

  private String[] ADMIN_LIST = new String[]{
      "/admin/**", "/users/{id}/update-role"
  };

  private String[] MANAGER_LIST = new String[]{
      "/manager/**"
  };

  @Value("${spring.profiles.front_url}")
  private String frontUrl;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(
        Role.ADMIN + " > " + Role.MANAGER + "\n" +
            Role.MANAGER + " > " + Role.USER
    );
    return roleHierarchy;
  }

  @Bean
  public WebSecurityCustomizer configure() {
    return (web) -> web.ignoring()
        .requestMatchers(
            new AntPathRequestMatcher("/h2-console/**"),  // H2 콘솔 직접 설정
            new AntPathRequestMatcher("/img/**"),
            new AntPathRequestMatcher("/css/**"),
            new AntPathRequestMatcher("/js/**")
        );
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      UserRepository userRepository) throws Exception {
    OAuth2Service oAuth2Service = applicationContext.getBean(OAuth2Service.class);

    http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
      CorsConfiguration config = new CorsConfiguration();
      config.addAllowedOrigin(frontUrl);
      config.addAllowedMethod("*");
      config.addAllowedHeader("*");
      config.setAllowCredentials(true);
      return config;
    }));

    // csrf disable
    http.csrf(AbstractHttpConfigurer::disable);
    // form 로그인 방식 disable
    http.formLogin(AbstractHttpConfigurer::disable);
    // http basic 인증 방식 disable
    http.httpBasic(AbstractHttpConfigurer::disable);

    http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));

    // oauth2
    http.oauth2Login((oauth2) -> oauth2
        .userInfoEndpoint((userInfoEndpointConfig) ->
            userInfoEndpointConfig.userService(oAuth2Service))
        .successHandler(successHandler));
//        .failureHandler(failureHandler));

    http.authorizeHttpRequests((auth) -> auth
        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
        // 전체 허용 API
        .requestMatchers(WHITE_LIST).permitAll()
        // ADMIN 전용 API
        .requestMatchers(ADMIN_LIST).hasAuthority(Role.ADMIN.name())
        // MANAGER 전용 API
        .requestMatchers(MANAGER_LIST).hasAuthority(Role.MANAGER.name())
        // ADMIN은 /carts/** 접근 불가
        .requestMatchers("/carts/**").access((authentication, context) ->
            new AuthorizationDecision(authentication.get().getAuthorities().stream()
                .noneMatch(
                    grantedAuthority -> grantedAuthority.getAuthority().equals(Role.ADMIN.name())))
        )
        // 기타 요청은 인증 필요
        .anyRequest().authenticated()
    );

    http.addFilterBefore(new JWTFilter(userRepository, tokenService, jwtUtil), CustomLoginFilter.class);
    http.addFilterBefore(new CustomLogoutFilter(jwtUtil), LogoutFilter.class);
    http.addFilterAt(
        new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
        UsernamePasswordAuthenticationFilter.class);

    // 세션 설정
    http.sessionManagement((session) ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    return http.build();
  }
}
