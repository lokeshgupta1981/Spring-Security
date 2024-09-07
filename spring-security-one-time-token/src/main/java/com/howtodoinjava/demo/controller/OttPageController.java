package com.howtodoinjava.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ott")
public class OttPageController {

  @GetMapping("/sent")
  String ottSent() {
    return "ott-sent";
  }
}
