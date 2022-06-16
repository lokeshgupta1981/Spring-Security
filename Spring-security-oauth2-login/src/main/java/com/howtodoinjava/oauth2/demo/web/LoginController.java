package com.howtodoinjava.oauth2.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String login() {
    return "oauth2-login.html";
  }
}
