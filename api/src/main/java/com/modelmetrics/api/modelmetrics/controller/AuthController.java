package com.modelmetrics.api.modelmetrics.controller;

import com.modelmetrics.api.modelmetrics.dto.auth.login.LoginAuthRequest;
import com.modelmetrics.api.modelmetrics.dto.auth.register.RegisterAuthRequest;
import com.modelmetrics.api.modelmetrics.dto.response.EntityChangeResponse;
import com.modelmetrics.api.modelmetrics.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** AuthController. */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<EntityChangeResponse> register(@RequestBody RegisterAuthRequest entity) {
    return ResponseEntity.ok(authService.register(entity));
  }

  @PostMapping("/login")
  public ResponseEntity<EntityChangeResponse> login(
      @RequestBody LoginAuthRequest entity, HttpServletResponse response) {
    return ResponseEntity.ok(authService.login(entity, response));
  }

  @GetMapping("/logout")
  public ResponseEntity<EntityChangeResponse> logout(HttpServletResponse response) {
    return ResponseEntity.ok(authService.logout(response));
  }

  @PostMapping("/verify")
  public ResponseEntity<EntityChangeResponse> verifyAccount(
      @RequestParam String token, @RequestParam UUID userId) {
    return ResponseEntity.ok(authService.verifyAccount(token, userId));
  }

  @PostMapping("/resend-verification")
  public ResponseEntity<EntityChangeResponse> resendVerificationEmail(@RequestParam String email) {
    return ResponseEntity.ok(authService.resendVerificationEmail(email));
  }
}
