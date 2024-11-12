package com.howtodoinjava.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@Slf4j
public class JokeController {

  @GetMapping(value = "/joke", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  @Operation(summary = "Returns a funny joke")
  public ResponseEntity<?> jokeRetrievalHandler() {

    final var response = new JSONObject();
    try {
      response.put("joke", "What kind of murderer has moral fiber? – A cereal killer.");
    } catch (JSONException e) {
      log.error("Unable to generate JSON response");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return ResponseEntity.ok(response.toString());
  }

}
