package com.example.playcation.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.example.playcation.enums.Role;
import com.example.playcation.filter.JWTFilter;
import com.example.playcation.filter.CustomLoginFilter;
import com.example.playcation.filter.CustomLogoutFilter;
import com.example.playcation.oauth2.handler.SuccessHandler;
import com.example.playcation.oauth2.service.OAuth2Service;
import com.example.playcation.user.repository.UserRepository;
import com.example.playcation.user.service.AdminService;
import com.example.playcation.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
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
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final SuccessHandler successHandler;
  private final OAuth2Service oAuth2Service;
  private final JWTUtil jwtUtil;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
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
    // http basic 인증 방식 disable
    http.httpBasic(AbstractHttpConfigurer::disable);

    http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));

    // oauth2
    http.oauth2Login((oauth2) -> oauth2
//        .loginPage("/oauth2-login").permitAll()
        .userInfoEndpoint((userInfoEndpointConfig) ->
            userInfoEndpointConfig.userService(oAuth2Service))
        .successHandler(successHandler));

    http.authorizeHttpRequests((auth) -> auth
        .requestMatchers("/", "*/sign-in", "/oauth2-login", "/refresh", "/error", "/token/refresh").permitAll()
        .requestMatchers("/h2-console/**").permitAll()
        // ADMIN 전용 API
        .requestMatchers("/users/{id}/update-role").hasAuthority(Role.ADMIN.name()) // "ADMIN"
        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name()) // "ADMIN"

        // MANAGER 전용 API
        .requestMatchers("/manager/**").hasAuthority(Role.MANAGER.name()) // "MANAGER"

        // ADMIN은 /games/** 접근 불가
        .requestMatchers("/games/**").access((authentication, context) ->
            new AuthorizationDecision(authentication.get().getAuthorities().stream()
                .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.ADMIN.name())))
        )
        // 기타 요청은 인증 필요
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
