package com.howtodoinjava;

import com.howtodoinjava.demo.security.basicauth.AppBasicAuthenticationEntryPoint;
import com.howtodoinjava.demo.security.providers.CustomIdentityAuthenticationProvider;
import com.howtodoinjava.demo.web.AppController;
import com.howtodoinjava.test.SpringTestContext;
import com.howtodoinjava.test.SpringTestContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringTestContextExtension.class)
public class TestCustomAuthenticationProvider {

  public final SpringTestContext spring = new SpringTestContext(this);

  @Autowired
  private MockMvc mvc;

  @BeforeEach
  public void setup() {
    spring.register(CustomAuthenticationConfig.class,
        AppController.class,
        CustomIdentityAuthenticationProvider.class).autowire();
  }

  @Test
  public void expectOKResponse_WhenAuthenticaionManagerIsTestedWithCorrectDetails() {
    AuthenticationManager authenticationManager = this.spring.getContext()
        .getBean(AuthenticationManager.class);
    Authentication authentication = authenticationManager
        .authenticate(UsernamePasswordAuthenticationToken
            .unauthenticated("user", "password"));
    assertThat(authentication.isAuthenticated()).isTrue();
  }

  @Test
  public void expectBadCredentialsException_WhenTestedWithInCorrectDetails() {
    AuthenticationManager authenticationManager = this.spring.getContext()
        .getBean(AuthenticationManager.class);

    Exception exception =
        assertThrows(BadCredentialsException.class, () -> {
          Authentication authentication = authenticationManager
              .authenticate(UsernamePasswordAuthenticationToken
                  .unauthenticated("user", "bad-password"));
        });
  }

  @EnableWebSecurity
  static class CustomAuthenticationConfig {

    @Autowired
    private CustomIdentityAuthenticationProvider customIdentityAuthenticationProvider;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
      AuthenticationManagerBuilder authenticationManagerBuilder =
          http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder.authenticationProvider(customIdentityAuthenticationProvider);
      return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.authorizeRequests()
          .requestMatchers("/public").permitAll()
          .anyRequest().authenticated()
          .and()
          .httpBasic()
          .authenticationEntryPoint(basicAuthenticationEntryPoint());

      return http.build();
    }

    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
      return new HandlerMappingIntrospector();
    }


    @Bean
    AppBasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
      return new AppBasicAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder(8);
    }
  }
}
