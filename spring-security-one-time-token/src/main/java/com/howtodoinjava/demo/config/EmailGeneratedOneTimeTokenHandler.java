package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.model.User;
import com.howtodoinjava.demo.service.CustomUserDetailsService;
import com.howtodoinjava.demo.service.EmailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.GeneratedOneTimeTokenHandler;
import org.springframework.security.web.authentication.ott.RedirectGeneratedOneTimeTokenHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class EmailGeneratedOneTimeTokenHandler implements GeneratedOneTimeTokenHandler {

  private final EmailService emailService;
  private final CustomUserDetailsService userDetailsService;
  private final GeneratedOneTimeTokenHandler redirectHandler
    = new RedirectGeneratedOneTimeTokenHandler("/ott/sent");

  public EmailGeneratedOneTimeTokenHandler(EmailService emailService,
    CustomUserDetailsService userDetailsService) {

    this.emailService = emailService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
    OneTimeToken oneTimeToken) throws IOException, ServletException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
      .replacePath(request.getContextPath())
      .replaceQuery(null)
      .fragment(null)
      .path("/login/ott")
      .queryParam("token", oneTimeToken.getTokenValue());
    String magicLink = builder.toUriString();
    String email = getUserEmail(oneTimeToken.getUsername());
    this.emailService.sendEmail(email, "One Time Token for the Spring Security Demo App",
      "Use the following link to sign in into the application: " + magicLink);
    this.redirectHandler.handle(request, response, oneTimeToken);
  }

  private String getUserEmail(String username) {
    User user = userDetailsService.loadUserByUsername(username);
    return user.getEmail();
  }
}
