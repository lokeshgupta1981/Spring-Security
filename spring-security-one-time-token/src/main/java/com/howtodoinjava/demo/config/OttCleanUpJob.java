package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.repository.OttTokenRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OttCleanUpJob {

  private final OttTokenRepository tokenRepository;

  public OttCleanUpJob(OttTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hour
  @Transactional
  public void cleanUpOldRecords() {
    System.out.println("Deleting expired tokens");
    int deleted = tokenRepository.deleteExpiredTokens(Instant.now());
    System.out.println("Deleted " + deleted + " expired tokens");
  }
}
