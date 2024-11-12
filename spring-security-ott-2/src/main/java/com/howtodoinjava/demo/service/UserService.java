package com.howtodoinjava.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.howtodoinjava.demo.constant.OtpContext;
import com.howtodoinjava.demo.dto.OtpVerificationRequestDto;
import com.howtodoinjava.demo.dto.TokenRefreshRequestDto;
import com.howtodoinjava.demo.dto.SignupRequestDto;
import com.howtodoinjava.demo.dto.LoginRequestDto;
import com.howtodoinjava.demo.dto.LoginSuccessDto;
import com.howtodoinjava.demo.entity.User;
import com.howtodoinjava.demo.mail.EmailService;
import com.howtodoinjava.demo.repository.UserRepository;
import com.howtodoinjava.demo.security.utility.JwtUtils;
import com.google.common.cache.LoadingCache;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final LoadingCache<String, Integer> oneTimePasswordCache;
  private final EmailService emailService;
  private final JwtUtils jwtUtils;

  public ResponseEntity<?> createAccount(
    final SignupRequestDto userAccountCreationRequestDto) {
    if (userRepository.existsByEmailId(userAccountCreationRequestDto.getEmailId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
        "User account already exists for provided email-id");
    }

    final var user = new User();
    user.setEmailId(userAccountCreationRequestDto.getEmailId());
    user.setPassword(passwordEncoder.encode(userAccountCreationRequestDto.getPassword()));
    user.setEmailVerified(false);
    user.setActive(true);
    final var savedUser = userRepository.save(user);

    sendOtp(savedUser, "Verify your account");
    return ResponseEntity.ok(getOtpSendMessage());
  }

  public ResponseEntity<?> login(final LoginRequestDto userLoginRequestDto) {
    final User user = userRepository.findByEmailId(userLoginRequestDto.getEmailId())
      .orElseThrow(
        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials"));

    if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials");
    }

    if (!user.isActive()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not active");
    }

    sendOtp(user, "2FA: Request to log in to your account");
    return ResponseEntity.ok(getOtpSendMessage());
  }

  public ResponseEntity<LoginSuccessDto> verifyOtp(
    final OtpVerificationRequestDto otpVerificationRequestDto) {
    User user = userRepository.findByEmailId(otpVerificationRequestDto.getEmailId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email-id"));

    Integer storedOneTimePassword = null;
    try {
      storedOneTimePassword = oneTimePasswordCache.get(user.getEmailId());
    } catch (ExecutionException e) {
      log.error("FAILED TO FETCH PAIR FROM OTP CACHE: ", e);
      throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    if (storedOneTimePassword.equals(otpVerificationRequestDto.getOneTimePassword())) {
      if (otpVerificationRequestDto.getContext().equals(OtpContext.SIGN_UP)) {
        user.setEmailVerified(true);
        user = userRepository.save(user);
        return ResponseEntity
          .ok(LoginSuccessDto.builder().accessToken(jwtUtils.generateAccessToken(user))
            .refreshToken(jwtUtils.generateRefreshToken(user)).build());
      } else if (otpVerificationRequestDto.getContext().equals(OtpContext.LOGIN)) {
        return ResponseEntity
          .ok(LoginSuccessDto.builder().accessToken(jwtUtils.generateAccessToken(user))
            .refreshToken(jwtUtils.generateRefreshToken(user)).build());
      } else if (otpVerificationRequestDto.getContext().equals(OtpContext.ACCOUNT_DELETION)) {
        user.setActive(false);
        user = userRepository.save(user);
        return ResponseEntity.ok().build();
      }
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<?> deleteAccount(final UUID userId) {
    final var user = userRepository.findById(userId).get();
    sendOtp(user, "2FA: Confirm account Deletion");
    return ResponseEntity.ok(getOtpSendMessage());
  }

  public ResponseEntity<?> getDetails(final UUID userId) {
    final var user = userRepository.findById(userId).get();
    final var response = new HashMap<String, String>();
    response.put("email_id", user.getEmailId());
    response.put("created_at", user.getCreatedAt().toString());
    return ResponseEntity.ok(response);
  }

  private void sendOtp(final User user, final String subject) {
    oneTimePasswordCache.invalidate(user.getEmailId());

    final var otp = new Random().ints(1, 100000, 999999).sum();
    oneTimePasswordCache.put(user.getEmailId(), otp);
    log.info("OTP sent :: {}", otp);

    CompletableFuture.supplyAsync(() -> {
      emailService.sendEmail(user.getEmailId(), subject, "OTP: " + otp);
      return HttpStatus.OK;
    }).thenAccept(status -> {
      System.out.println("Email sent successfully with status: " + status);
    });
  }

  private Map<String, String> getOtpSendMessage() {
    final var response = new HashMap<String, String>();
    response.put("message",
      "OTP sent successfully sent to your registered email-address. verify it using /verify-otp endpoint");
    return response;
  }

  public ResponseEntity<?> refreshToken(final TokenRefreshRequestDto tokenRefreshRequestDto) {
    if (jwtUtils.isTokenExpired(tokenRefreshRequestDto.getRefreshToken())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
    }
    final var user = userRepository.findByEmailId(
        jwtUtils.extractEmail(tokenRefreshRequestDto.getRefreshToken()))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    return ResponseEntity.ok(
      LoginSuccessDto.builder().refreshToken(tokenRefreshRequestDto.getRefreshToken())
        .accessToken(jwtUtils.generateAccessToken(user)).build());
  }

}
