package com.howtodoinjava;

import com.howtodoinjava.demo.security.basicauth.AppBasicAuthenticationEntryPoint;
import com.howtodoinjava.demo.web.AppController;
import com.howtodoinjava.test.SpringTestContext;
import com.howtodoinjava.test.SpringTestContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringTestContextExtension.class)
public class BasicAuthUsingInMemoryUserDetails {

    public final SpringTestContext spring = new SpringTestContext(this);

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        spring.register(SecurityConfigWithDefaults.class).autowire();
    }

    @Test
    void contextLoads() throws Exception {
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
    void expectOKResponse_WhenAccessNotSecuredURL() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/public"))
                .andExpect(status().isOk());
    }

    @Test
    void expectUnauthorizedUser_WhenPasswordIsWrong() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/")
                        .with(httpBasic("user", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expectOKResponse_WhenPasswordIsCorrect() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/")
                        .with(httpBasic("user", "password")))
                .andExpect(content().string("Hello World !!"));
    }

    @EnableWebSecurity(debug = true)
    static class SecurityConfigWithDefaults extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            UserDetails user = User
                    .withUsername("user")
                    .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
                    .roles("USER_ROLE")
                    .build();
            auth
                    .inMemoryAuthentication()
                    .withUser(user);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/public").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic()
                    .authenticationEntryPoint(basicAuthenticationEntryPoint());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        AppController appController() {
            return new AppController();
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


