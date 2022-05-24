package com.howtodoinjava.demo.web;

import com.howtodoinjava.demo.security.basicauth.AppBasicAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AppController {

  @Autowired
  private AppBasicAuthenticationEntryPoint logoutHandler;

  @GetMapping("/")
  public String home(){
    return "Hello World !!";
  }

  @GetMapping("/public")
  public String publicResource(){
    return "Access allowed to all !!";
  }
}
