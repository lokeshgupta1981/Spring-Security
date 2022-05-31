package com.howtodoinjava.app.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @PostMapping("/login_success_handler")
  public String loginSuccessHandler() {
    //perform audit actions
    return "/";
  }

  @PostMapping("/login_failure_handler")
  public String loginFailureHandler() {
    //perform audit actions
    return "login";
  }
}
