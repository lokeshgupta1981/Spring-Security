package com.howtodoinjava.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.util.Assert;

@Entity(name = "ott")
@Table(name = "tbl_ott_token")
@Data
@NoArgsConstructor
public class OttToken implements OneTimeToken {

  public OttToken(String token, String username, Instant expireAt) {
    Assert.hasText(token, "token cannot be empty");
    Assert.hasText(username, "username cannot be empty");
    Assert.notNull(expireAt, "expireAt cannot be null");
    this.tokenValue = token;
    this.username = username;
    this.expiresAt = expireAt;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String username;

  @Column
  private String tokenValue;

  @Column
  private Instant expiresAt;
}
