package com.howtodoinjava.oauth2.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public ClientRegistrationRepository clientRepository() {

    ClientRegistration githubRegistration =
        CommonOAuth2Provider.GITHUB.getBuilder("github")
            .clientId("id")
            .clientSecret("secret")
            .build();

    ClientRegistration facebookRegistration =
        CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
            .clientId("id")
            .clientSecret("secret")
            .build();

    return new InMemoryClientRegistrationRepository(githubRegistration,
        facebookRegistration);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests(a -> a
            .antMatchers("/login", "/error", "/webjars/**").permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(e -> e
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        .csrf(c -> c
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        .logout(l -> l
            .logoutSuccessUrl("/login").permitAll()
        )
        .oauth2Login()
        .loginPage("/login");
  }
}
