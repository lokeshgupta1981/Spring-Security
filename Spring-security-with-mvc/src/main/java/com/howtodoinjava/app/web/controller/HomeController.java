package com.howtodoinjava.app.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/home")
  public String home() {
    return "home";
  }

  @GetMapping("/")
  public String homePage() {
    return "redirect:home";
  }
}
