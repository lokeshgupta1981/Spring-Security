package com.howtodoinjava.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class LoginSuccessDto {

  private final String accessToken;
  private final String refreshToken;
}
