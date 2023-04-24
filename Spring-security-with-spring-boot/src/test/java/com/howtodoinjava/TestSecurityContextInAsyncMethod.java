package com.howtodoinjava;

import com.howtodoinjava.test.SpringTestContext;
import com.howtodoinjava.test.SpringTestContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringTestContextExtension.class)
public class TestSecurityContextInAsyncMethod {

  public final SpringTestContext spring = new SpringTestContext(this);

  @Autowired
  private MockMvc mvc;

  @BeforeEach
  public void setup() {
    spring.register(AsyncAuthenticationConfig.class,
        WebController.class,
        AsyncService.class).autowire();
  }

  @Test
  void contextLoads() throws Exception {
  }

  @Test
  public void
  expectOK_WhenAuthenticaionManagerIsTested_WithCorrectCredentials() {
    AuthenticationManager authenticationManager = this.spring.getContext()
        .getBean(AuthenticationManager.class);
    Authentication authentication = authenticationManager
        .authenticate(UsernamePasswordAuthenticationToken
            .unauthenticated("user", "password"));
    assertThat(authentication.isAuthenticated()).isTrue();
  }

  @Test
  void expectOK_WhenAccessAsyncAPI() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/async")
            .with(httpBasic("user", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void expectOK_WhenAccessAPI_CreatingNewCallable() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/asyncCallable")
            .with(httpBasic("user", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void expectOK_WhenAccessAPI_CreatingNewRunnable() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/asyncRunnable")
            .with(httpBasic("user", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void expectOK_WhenAccessAPI_CreatingNewExecutorService() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/asyncExecutor")
            .with(httpBasic("user", "password")))
        .andExpect(status().isOk());
  }

  @EnableWebSecurity
  @EnableAsync
  static class AsyncAuthenticationConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.httpBasic();
      http.authorizeRequests().anyRequest().authenticated();
      return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
      AuthenticationManagerBuilder authenticationManagerBuilder =
          http.getSharedObject(AuthenticationManagerBuilder.class);

      UserDetails user = User
          .withUsername("user")
          .password(passwordEncoder().encode("password"))
          .roles("USER_ROLE")
          .build();

      authenticationManagerBuilder.inMemoryAuthentication().withUser(user);
      return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder(8);
    }

    //1

    /*@Bean
    public InitializingBean initializingBean() {
      return () -> SecurityContextHolder.setStrategyName(
          SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }*/

    //2

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(10);
      executor.setMaxPoolSize(100);
      executor.setQueueCapacity(50);
      executor.setThreadNamePrefix("async-");
      return executor;
    }

    @Bean
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor
        (ThreadPoolTaskExecutor delegate) {
      return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }
  }
}

@RestController
class WebController {

  Logger log = LoggerFactory.getLogger(WebController.class);

  @Autowired
  AsyncService asyncService;

  @GetMapping(value = "/async")
  public void executeWithInternalThread() throws Exception {

    log.info("In executeWithInternalThread - before call: "
        + SecurityContextHolder.getContext().getAuthentication().getName());

    asyncService.checkIfPrincipalPropagated();

    log.info("In executeWithInternalThread - after call: "
        + SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @GetMapping(value = "/asyncCallable")
  public String executeWithExternalThread_Callable()
      throws ExecutionException, InterruptedException {

    log.info("In executeWithExternalThread_Callable: "
        + SecurityContextHolder.getContext().getAuthentication().getName());

    return asyncService.checkIfPrincipalPropagatedToNewCallable();
  }

  @GetMapping(value = "/asyncRunnable")
  public void executeWithExternalThread_Runnable() {

    log.info("In executeWithExternalThread_Runnable: "
        + SecurityContextHolder.getContext().getAuthentication().getName());

    asyncService.checkIfPrincipalPropagatedToNewRunnable();
  }

  @GetMapping(value = "/asyncExecutor")
  public void executeWithExternalThread_Executor() {

    log.info("In executeWithExternalThread_Executor: "
        + SecurityContextHolder.getContext().getAuthentication().getName());

    asyncService.checkIfPrincipalPropagatedToExecutorService();
  }
}

@Service
class AsyncService {

  Logger log = LoggerFactory.getLogger(AsyncService.class);

  @Async
  public void checkIfPrincipalPropagated() {
    String username = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();

    log.info("In checkIfPrincipalPropagated: "
        + username);
  }

  public String checkIfPrincipalPropagatedToNewCallable()
      throws ExecutionException, InterruptedException {

    Callable<String> task = () -> {

      String username = SecurityContextHolder.getContext()
          .getAuthentication()
          .getName();

      log.info("In checkIfPrincipalPropagatedToNewCallable inside Callable: "
          + username);

      return username;
    };

    ExecutorService e = Executors.newCachedThreadPool();
    try {
      var contextTask = new DelegatingSecurityContextCallable<>(task);
      return "Ciao, " + e.submit(contextTask).get() + "!";
    } finally {
      e.shutdown();
    }
  }

  public void checkIfPrincipalPropagatedToNewRunnable() {

    Runnable task = () -> {

      String username = SecurityContextHolder.getContext()
          .getAuthentication()
          .getName();

      log.info("In checkIfPrincipalPropagatedToNewRunnable inside Runnable: "
          + username);
    };

    ExecutorService e = Executors.newCachedThreadPool();
    try {
      var contextTask = new DelegatingSecurityContextRunnable(task);
      e.submit(contextTask);
    } finally {
      e.shutdown();
    }
  }

  public void checkIfPrincipalPropagatedToExecutorService() {

    Runnable task = () -> {
      String username = SecurityContextHolder.getContext()
          .getAuthentication()
          .getName();

      log.info("In checkIfPrincipalPropagatedToExecutorService inside " +
          "Runnable: "
          + username);
    };

    ExecutorService e = Executors.newCachedThreadPool();
    e = new DelegatingSecurityContextExecutorService(e);

    try {
      e.submit(task);
    } finally {
      e.shutdown();
    }
  }
}
