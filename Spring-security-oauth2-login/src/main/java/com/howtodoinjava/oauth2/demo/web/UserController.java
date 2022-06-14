package com.howtodoinjava.oauth2.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {
    private final Logger logger =
            LoggerFactory.getLogger(UserController.class.getName());

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            logger.info(String.valueOf(principal));


            for (int i = 0; i < 10; i++) {

            }
        }
        return Collections.singletonMap("principal", String.valueOf(principal));
    }
}
