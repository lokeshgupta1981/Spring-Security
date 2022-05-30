package com.howtodoinjava.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
      .withUser("user1").password(passwordEncoder().encode("user1Pass")).roles("USER")
      .and()
      .withUser("user2").password(passwordEncoder().encode("user2Pass")).roles("USER")
      .and()
      .withUser("admin").password(passwordEncoder().encode("adminPass")).roles("ADMIN");
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.authorizeRequests()
      .antMatchers("/login", "/register")
      .permitAll()
      .antMatchers("/account/**").access("hasRole('ROLE_ADMIN')")
      .and()
      .formLogin(form -> form
        .loginPage("/login")
        .defaultSuccessUrl("/home")
        .failureUrl("/login?error=true")
      );
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
      .antMatchers("/resources/**", "/static/**");
  }

  @Bean
  public LogoutSuccessHandler logoutSuccessHandler() {
    return new CustomLogoutSuccessHandler();
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  @Bean
  public AuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
