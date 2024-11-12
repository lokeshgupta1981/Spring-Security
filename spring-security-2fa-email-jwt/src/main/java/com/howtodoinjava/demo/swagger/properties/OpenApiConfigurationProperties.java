package com.howtodoinjava.demo.swagger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.howtodoinjava.app")
public class OpenApiConfigurationProperties {

  private Swagger swagger = new Swagger();

  @Data
  public static class Swagger {

    private String title;
    private String description;
    private String apiVersion;
    private Contact contact = new Contact();
    private Security security = new Security();

    @Data
    public static class Contact {

      private String email;
      private String name;
      private String url;
    }

    @Data
    public static class Security {

      private String name;
      private String scheme;
      private String bearerFormat;
    }
  }
}