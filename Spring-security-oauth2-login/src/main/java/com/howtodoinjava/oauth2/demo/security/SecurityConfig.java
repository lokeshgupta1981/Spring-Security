package com.howtodoinjava.oauth2.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /*@Bean
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
  }*/

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeRequests(a -> a
        .requestMatchers("/**login**", "/error", "/webjars/**", "/templates/**").permitAll()
        .anyRequest().authenticated()
      )
      /*.exceptionHandling(e -> e
          .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
      )*/
      .csrf(c -> c
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      )
      .logout(l -> l
        .logoutSuccessUrl("/login").permitAll()
      )
      .oauth2Login(oauth2 -> oauth2.loginPage("/login"));

    return http.build();
  }
}
