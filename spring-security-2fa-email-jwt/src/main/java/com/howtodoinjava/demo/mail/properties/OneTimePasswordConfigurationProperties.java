package com.howtodoinjava.demo.mail.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.howtodoinjava.app")
public class OneTimePasswordConfigurationProperties {

  private OTP otp = new OTP();

  @Data
  public class OTP {

    private Integer expirationMinutes;
  }
}