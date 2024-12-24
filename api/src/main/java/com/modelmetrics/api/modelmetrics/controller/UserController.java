package com.modelmetrics.api.modelmetrics.controller;

import com.modelmetrics.api.modelmetrics.dto.email.EmailResetRequest;
import com.modelmetrics.api.modelmetrics.dto.email.EmailResetVerificationRequest;
import com.modelmetrics.api.modelmetrics.dto.password.PasswordChangeRequest;
import com.modelmetrics.api.modelmetrics.dto.password.PasswordResetVerifyRequest;
import com.modelmetrics.api.modelmetrics.dto.response.EntityChangeResponse;
import com.modelmetrics.api.modelmetrics.dto.user.UserDto;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** UserController. */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(userService.getCurrentUser(user));
  }

  @PutMapping("/me")
  public ResponseEntity<UserDto> updateCurrentUser(
      @AuthenticationPrincipal User user, @Valid @RequestBody UserDto userDto) {
    return ResponseEntity.ok(userService.updateCurrentUser(user, userDto));
  }

  @PutMapping("/me/email")
  public ResponseEntity<EntityChangeResponse> sendEmailResetToken(
      @AuthenticationPrincipal User user, @RequestBody EmailResetRequest emailResetReq) {
    EntityChangeResponse response = userService.sendEmailResetToken(user, emailResetReq);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }

  @PutMapping("/email/verify")
  public ResponseEntity<EntityChangeResponse> changeEmail(
      @RequestBody EmailResetVerificationRequest emailResetVerification) {
    EntityChangeResponse response = userService.changeEmail(emailResetVerification);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }

  @PutMapping("/me/email/cancel-reset")
  public ResponseEntity<EntityChangeResponse> cancelEmailResetToken(
      @AuthenticationPrincipal User user) {
    EntityChangeResponse response = userService.cancelEmailResetToken(user);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }

  @PutMapping("/me/password")
  public ResponseEntity<EntityChangeResponse> sendPasswordResetOtp(
      @AuthenticationPrincipal User user) {
    EntityChangeResponse response = userService.sendPasswordResetOtp(user);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }

  @PutMapping("/me/password/verify")
  public ResponseEntity<EntityChangeResponse> verifyPasswordResetOtp(
      @AuthenticationPrincipal User user, @RequestBody PasswordResetVerifyRequest request) {
    EntityChangeResponse response = userService.verifyPasswordResetOtp(user, request);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }

  @PutMapping("/me/password/change")
  public ResponseEntity<EntityChangeResponse> changePassword(
      @AuthenticationPrincipal User user, @RequestBody PasswordChangeRequest request) {
    EntityChangeResponse response = userService.changePassword(user, request);
    return ResponseEntity.status(response.getStatusCode()).body(response);
  }
}
