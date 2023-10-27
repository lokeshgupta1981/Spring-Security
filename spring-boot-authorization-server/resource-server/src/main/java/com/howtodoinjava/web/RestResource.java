package com.howtodoinjava.web;

import com.howtodoinjava.model.UserProfile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestResource {

  @GetMapping("/api/users/me")
  public ResponseEntity<UserProfile> profile() {
    Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    UserProfile profile = new UserProfile();
    profile.setName(jwt.getSubject());

    return ResponseEntity.ok(profile);
  }
}
