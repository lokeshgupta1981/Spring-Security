package com.howtodoinjava.demo.controller;

import java.security.Principal;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecuredController {

  @GetMapping("/")
  String hello(Authentication authentication, Model model) {

    model.addAttribute("principal", authentication.getPrincipal());
    return "principal-info";
  }
}
