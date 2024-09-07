package com.howtodoinjava.demo.repository;

import com.howtodoinjava.demo.model.OttToken;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OttTokenRepository extends JpaRepository<OttToken, Long> {

  Optional<OttToken> findByTokenValue(String tokenValue);

  @Modifying
  @Transactional
  @Query("DELETE FROM ott e WHERE e.expiresAt < :currentTimestamp")
  int deleteExpiredTokens(Instant currentTimestamp);
}
