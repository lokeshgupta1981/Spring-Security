package com.howtodoinjava;

import com.howtodoinjava.demo.security.basicauth.AppBasicAuthenticationEntryPoint;
import com.howtodoinjava.demo.security.basicauth.dao.AppUserRepository;
import com.howtodoinjava.demo.security.basicauth.model.AppUser;
import com.howtodoinjava.demo.security.basicauth.service.CustomUserDetailsService;
import com.howtodoinjava.demo.web.AppController;
import com.howtodoinjava.test.SpringTestContext;
import com.howtodoinjava.test.SpringTestContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringTestContextExtension.class)
public class BasicAuthUsingCustomJdbcUserDetails {

  public final SpringTestContext spring = new SpringTestContext(this);

  @Autowired
  private AppUserRepository repo;

  @Autowired
  private MockMvc mvc;

  @BeforeEach
  public void setup() {
    spring.register(SecurityConfigWithDefaults.class).autowire();

    Optional<AppUser> user = repo.findByEmail("user@email.com");
    if (user.isPresent() == false) {
      AppUser appUser = new AppUser();
      appUser.setPassword("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
      appUser.setUsername("user");
      appUser.setEmail("user@email.com");
      repo.save(appUser);
    }
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
            .unauthenticated("user@email.com", "password"));
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
            .with(httpBasic("user@email.com", "wrong-password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void expectUnauthorizedUser_WhenEmailIsWrong() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/")
            .with(httpBasic("user-wrong@email.com", "password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void expectOKResponse_WhenPasswordIsCorrect() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/")
            .with(httpBasic("user@email.com", "password")))
        .andExpect(content().string("Hello World !!"));
  }

  @EnableWebSecurity
  @EnableJpaRepositories("com.howtodoinjava.demo.security.basicauth.dao")
  static class SecurityConfigWithDefaults {

    @Bean
    public DataSource dataSource() {
      return new EmbeddedDatabaseBuilder()
          .setType(EmbeddedDatabaseType.H2)
          .build();
    }

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
      CustomUserDetailsService service = new CustomUserDetailsService();
      return service;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
      AuthenticationManagerBuilder authenticationManagerBuilder =
          http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder.userDetailsService(customUserDetailsService());
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
      return new BCryptPasswordEncoder(11);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      vendorAdapter.setGenerateDdl(true);
      LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
      factory.setJpaVendorAdapter(vendorAdapter);
      factory.setPackagesToScan("com.howtodoinjava.demo.security.basicauth.model");
      factory.setDataSource(dataSource());
      factory.afterPropertiesSet();
      return factory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
      JpaTransactionManager txManager = new JpaTransactionManager();
      txManager.setEntityManagerFactory(entityManagerFactory());
      return txManager;
    }

  }
}


