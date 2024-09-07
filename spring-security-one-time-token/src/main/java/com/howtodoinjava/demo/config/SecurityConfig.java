package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.service.CustomOneTimeTokenService;
import com.howtodoinjava.demo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
    EmailGeneratedOneTimeTokenHandler oneTimeTokenHandler,
    CustomOneTimeTokenService customOneTimeTokenService) throws Exception {

    http.authorizeHttpRequests((authorize) -> authorize
        .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/ott/sent")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/api/**")).hasAnyRole("USER", "ADMIN")
        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
        .anyRequest().authenticated()
      )
      .formLogin(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .headers(httpSecurityHeadersConfigurer -> {
        httpSecurityHeadersConfigurer.frameOptions(FrameOptionsConfig::disable);
      })
      .logout(Customizer.withDefaults())
      .oneTimeTokenLogin(configurer -> configurer
        .generatedOneTimeTokenHandler(oneTimeTokenHandler)
        .oneTimeTokenService(customOneTimeTokenService)
      );

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(CustomUserDetailsService userDetailsService,
    PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(daoAuthenticationProvider);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
