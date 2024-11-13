package com.howtodoinjava.demo.security.utility;

import com.howtodoinjava.demo.entity.User;
import com.howtodoinjava.demo.security.configuration.properties.JwtConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@EnableConfigurationProperties(JwtConfigurationProperties.class)
@AllArgsConstructor
public class JwtUtils {

  private final JwtConfigurationProperties jwtConfigurationProperties;

  public String extractEmail(final String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public UUID extractUserId(final String token) {
    return UUID.fromString((String) extractAllClaims(token).get("user_id"));
  }

  public Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    String secretKey = jwtConfigurationProperties.getJwt().getSecretKey();
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    return Jwts.parser()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public Boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateAccessToken(final User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("account_creation_timestamp", user.getCreatedAt().toString());
    claims.put("user_id", user.getId());
    claims.put("email_id", user.getEmailId());
    claims.put("email_verified", user.isEmailVerified());
    return createToken(claims, user.getEmailId(), TimeUnit.HOURS.toMillis(1));
  }

  public String generateRefreshToken(final User user) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, user.getEmailId(), TimeUnit.DAYS.toMillis(15));
  }

  private String createToken(final Map<String, Object> claims, final String subject,
    final Long expiration) {
    String secretKey = jwtConfigurationProperties.getJwt().getSecretKey();
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
      .setClaims(claims)
      .setSubject(subject)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + expiration))
      .signWith(key)
      .compact();
  }

  public Boolean validateToken(final String token, final UserDetails userDetails) {
    final String username = extractEmail(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}