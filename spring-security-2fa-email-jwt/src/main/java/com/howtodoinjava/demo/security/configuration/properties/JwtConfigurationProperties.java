package com.howtodoinjava.demo.security.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "com.howtodoinjava.app")
@Data
public class JwtConfigurationProperties {

  private JWT jwt = new JWT();

  @Data
  public class JWT {

    private String secretKey;
  }
}