package com.howtodoinjava.demo.controller;

import com.howtodoinjava.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.howtodoinjava.demo.security.utility.JwtUtils;
import com.howtodoinjava.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserController {

  private final UserService userService;
  private final JwtUtils jwtUtils;

  @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  @Operation(summary = "Returns logged in users account details")
  public ResponseEntity<?> loggedInUserDetailsRetrievalHandler(
    @Parameter(hidden = true)
    @RequestHeader(required = true, name = "Authorization") final String header) {

    return userService.getDetails(jwtUtils.extractUserId(header));
  }

  @DeleteMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  @Operation(summary = "Deletes a user account")
  public ResponseEntity<?> userAccountDeletionHandler(
    @Parameter(hidden = true)
    @RequestHeader(required = true, name = "Authorization") final String header) {

    return userService.deleteAccount(jwtUtils.extractUserId(header));
  }
}
