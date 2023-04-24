package com.howtodoinjava.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private DataSource dataSource;

/*  @Override
  public void configure(AuthenticationManagerBuilder auth)
      throws Exception {

    auth.jdbcAuthentication()
        .dataSource(dataSource)
        .withDefaultSchema()
        .withUser(User.withUsername("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER"));
  }*/

  @Bean
  public UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
    return new JdbcUserDetailsManager(dataSource);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    //@formatter:off
    http.authorizeRequests()
        .requestMatchers("/login").permitAll()
        .requestMatchers("/**").hasAnyRole("USER", "ADMIN")
        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
        .and()
          .formLogin()
          .loginPage("/login")
          .loginProcessingUrl("/process-login")
          .defaultSuccessUrl("/home")
          .failureUrl("/login?error=true")
          .permitAll()
        .and()
          .logout()
          .logoutSuccessUrl("/login?logout=true")
          .invalidateHttpSession(true)
          .deleteCookies("JSESSIONID")
          .permitAll()
        .and()
          .csrf()
          .disable();
    //@formatter:on

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers("/resources/**", "/static/**");
  }



/*  @Bean
  AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new CustomAuthenticationSuccessHandler();
  }

  @Bean
  AuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
  }*/
}
