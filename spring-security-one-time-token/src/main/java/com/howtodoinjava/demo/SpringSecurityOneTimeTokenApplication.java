package com.howtodoinjava.demo;

import com.howtodoinjava.demo.model.User;
import com.howtodoinjava.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringSecurityOneTimeTokenApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(SpringSecurityOneTimeTokenApplication.class, args);
  }

  UserRepository userRepository;

  public SpringSecurityOneTimeTokenApplication(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void run(String... args) throws Exception {

    User user = new User();
    user.setFirstName("Lokesh");
    user.setLastName("Gupta");
    user.setEmail("howtodoinjava@gmail.com");
    user.setPhone("123456789");
    user.setUsername("testuser");
    user.setPassword("password");

    userRepository.save(user);
  }
}

