package com.howtodoinjava.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User implements Serializable {

  private static final long serialVersionUID = -3492266417550204992L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, unique = true)
  private UUID id;

  @Column(name = "email_id", nullable = false, unique = true)
  private String emailId;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "email_verified", nullable = false)
  private boolean isEmailVerified;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  void setUp() {
    this.createdAt = LocalDateTime.now(ZoneId.of("+00:00"));
  }
}
