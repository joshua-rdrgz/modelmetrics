package com.modelmetrics.api.modelmetrics.service.user.impl;

import com.modelmetrics.api.modelmetrics.dto.email.EmailResetRequest;
import com.modelmetrics.api.modelmetrics.dto.email.EmailResetVerificationRequest;
import com.modelmetrics.api.modelmetrics.dto.password.PasswordChangeRequest;
import com.modelmetrics.api.modelmetrics.dto.password.PasswordResetVerifyRequest;
import com.modelmetrics.api.modelmetrics.dto.response.EntityChangeResponse;
import com.modelmetrics.api.modelmetrics.dto.user.UserDto;
import com.modelmetrics.api.modelmetrics.entity.EmailResetToken;
import com.modelmetrics.api.modelmetrics.entity.PasswordResetOtp;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.repository.emailresettoken.EmailResetTokenRepository;
import com.modelmetrics.api.modelmetrics.repository.passwordresetotp.PasswordResetOtpRepository;
import com.modelmetrics.api.modelmetrics.repository.user.UserRepository;
import com.modelmetrics.api.modelmetrics.service.email.EmailService;
import com.modelmetrics.api.modelmetrics.service.user.UserService;
import com.modelmetrics.api.modelmetrics.util.TimeUtil;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UserServiceImpl. */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final EmailResetTokenRepository emailResetTokenRepository;
  private final PasswordResetOtpRepository passwordResetOtpRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDto getCurrentUser(User user) {
    EmailResetToken emailResetToken = emailResetTokenRepository.findByUserId(user.getId());
    PasswordResetOtp passwordResetOtp = passwordResetOtpRepository.findByUserId(user.getId());
    return UserDto.fromUser(user, emailResetToken, passwordResetOtp);
  }

  @Override
  public UserDto updateCurrentUser(User user, UserDto userDto) {
    if (userDto.getEmail() != null) {
      throw new IllegalArgumentException("This endpoint does not support updating emails.");
    }

    User managedUser = userRepository.findFirstById(user.getId());

    managedUser.setFirstName(userDto.getFirstName());
    managedUser.setLastName(userDto.getLastName());
    managedUser.setCurrency(userDto.getCurrency());
    managedUser.setTaxAllocationPercentage(userDto.getTaxAllocationPercentage());

    User updatedUser = userRepository.save(managedUser);

    EmailResetToken emailResetToken = emailResetTokenRepository.findByUserId(managedUser.getId());
    PasswordResetOtp passwordResetOtp =
        passwordResetOtpRepository.findByUserId(managedUser.getId());

    return UserDto.fromUser(updatedUser, emailResetToken, passwordResetOtp);
  }

  @Override
  public EntityChangeResponse sendEmailResetToken(User user, EmailResetRequest emailReset) {
    User managedUser = userRepository.findFirstById(user.getId());

    EmailResetToken existingToken = emailResetTokenRepository.findByUserId(managedUser.getId());
    if (existingToken != null && existingToken.getExpiryDate().isAfter(LocalDateTime.now())) {
      return sendErrorResponseWithMessage(
          "Could not send email reset token. Please try again later.");
    }

    String token = UUID.randomUUID().toString();
    EmailResetToken emailResetToken =
        EmailResetToken.builder()
            .user(managedUser)
            .token(passwordEncoder.encode(token))
            .newEmail(emailReset.getEmail())
            .expiryDate(LocalDateTime.now().plusMinutes(30))
            .build();

    emailResetTokenRepository.save(emailResetToken);

    emailService.sendEmailChangeEmail(emailReset.getEmail(), token, managedUser.getId());

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("Email reset token sent successfully.")
        .build();
  }

  @Override
  public EntityChangeResponse changeEmail(EmailResetVerificationRequest emailResetVerification) {
    User managedUser = userRepository.findFirstById(emailResetVerification.getUserId());
    EmailResetToken emailResetToken = emailResetTokenRepository.findByUserId(managedUser.getId());

    if (managedUser == null
        || emailResetToken == null
        || !passwordEncoder.matches(
            emailResetVerification.getToken(), emailResetToken.getToken())) {
      return sendErrorResponseWithMessage("Could not change email.");
    }

    managedUser.setEmail(emailResetToken.getNewEmail());
    userRepository.save(managedUser);

    emailResetTokenRepository.delete(emailResetToken);

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("Email changed successfully.")
        .build();
  }

  @Override
  public EntityChangeResponse cancelEmailResetToken(User user) {
    EmailResetToken emailResetToken = emailResetTokenRepository.findByUserId(user.getId());

    if (emailResetToken == null) {
      return sendErrorResponseWithMessage("No email reset token found for the user.");
    }

    emailResetTokenRepository.delete(emailResetToken);

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("Email reset token canceled successfully.")
        .build();
  }

  @Override
  public EntityChangeResponse sendPasswordResetOtp(User user) {
    User managedUser = userRepository.findFirstById(user.getId());

    if (isOnCompletionCooldown(managedUser)) {
      return sendGenericErrorResponse();
    }

    PasswordResetOtp existingOtp = passwordResetOtpRepository.findByUserId(managedUser.getId());

    // OTP exists and is NOT expired
    if (existingOtp != null && existingOtp.getExpiryDate().isAfter(LocalDateTime.now())) {
      return sendGenericErrorResponse();
    }

    // OTP exists and unfinished cooldown
    if (existingOtp != null
        && existingOtp.getCooldownComplete() != null
        && existingOtp.getCooldownComplete().isAfter(LocalDateTime.now())) {
      Optional<Long> minutesRemaining =
          TimeUtil.minutesBetween(LocalDateTime.now(), existingOtp.getCooldownComplete());

      return sendErrorResponseWithMessage(
          String.format(
              "You must wait %d minutes before requesting a new password reset OTP.",
              minutesRemaining.get()));
    }

    // OTP exists and IS expired
    if (existingOtp != null && existingOtp.getExpiryDate().isBefore(LocalDateTime.now())) {
      passwordResetOtpRepository.delete(existingOtp);
    }

    String otp = generateOtp();

    PasswordResetOtp passwordResetOtp =
        PasswordResetOtp.builder()
            .user(managedUser)
            .otp(passwordEncoder.encode(otp))
            .expiryDate(LocalDateTime.now().plusMinutes(15))
            .build();

    passwordResetOtpRepository.save(passwordResetOtp);
    emailService.sendPasswordResetOtpEmail(managedUser.getEmail(), otp);

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("OTP sent successfully.")
        .build();
  }

  @Override
  public EntityChangeResponse verifyPasswordResetOtp(
      User user, PasswordResetVerifyRequest request) {
    User managedUser = userRepository.findFirstById(user.getId());

    if (isOnCompletionCooldown(managedUser)) {
      return sendGenericErrorResponse();
    }

    PasswordResetOtp otp = passwordResetOtpRepository.findByUserId(managedUser.getId());

    // OTP doesn't exist
    if (otp == null) {
      return sendGenericErrorResponse();
    }

    // OTP is expired
    if (otp.getExpiryDate().isBefore(LocalDateTime.now())) {
      return sendErrorResponseWithMessage("OTP is expired, please request a new one.");
    }

    // OTP is invalid
    if (!passwordEncoder.matches(request.getOtp(), otp.getOtp())) {
      int newAttemptsRemaining = otp.getAttemptsRemaining() - 1;
      otp.setAttemptsRemaining(newAttemptsRemaining);

      String message;

      // Add cooldown_complete field if no more attempts
      if (newAttemptsRemaining <= 0) {
        LocalDateTime now = LocalDateTime.now();

        otp.setCooldownComplete(now.plusMinutes(30));
        otp.setExpiryDate(now);

        message = "Too many attempts.  Please wait 30 minutes, then request a new OTP.";
      } else {
        message =
            String.format("Invalid OTP, you have %d attempts remaining.", newAttemptsRemaining);
      }

      // Save all changes
      passwordResetOtpRepository.save(otp);

      return sendErrorResponseWithMessage(message);
    }

    // OTP is valid.
    otp.setVerified(true);
    passwordResetOtpRepository.save(otp);

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("OTP verified successfully.")
        .build();
  }

  @Override
  public EntityChangeResponse changePassword(User user, PasswordChangeRequest request) {
    User managedUser = userRepository.findFirstById(user.getId());

    if (isOnCompletionCooldown(managedUser)) {
      return sendGenericErrorResponse();
    }

    PasswordResetOtp otp = passwordResetOtpRepository.findByUserId(managedUser.getId());

    // OTP doesn't exist, or
    // OTP isn't verified, or
    // OTP is expired.
    if (otp == null || !otp.getVerified() || otp.getExpiryDate().isBefore(LocalDateTime.now())) {
      return sendGenericErrorResponse();
    }

    managedUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
    managedUser.setLastPasswordChange(LocalDateTime.now());
    userRepository.save(managedUser);
    passwordResetOtpRepository.delete(otp);

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("Password changed successfully.")
        .build();
  }

  private String generateOtp() {
    return String.format("%06d", new SecureRandom().nextInt(1000000));
  }

  private boolean isOnCompletionCooldown(User user) {
    if (user.getLastPasswordChange() != null) {
      LocalDateTime cooldownEnd = user.getLastPasswordChange().plusDays(1);
      return LocalDateTime.now().isBefore(cooldownEnd);
    }
    return false;
  }

  private EntityChangeResponse sendGenericErrorResponse() {
    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(400))
        .status("fail")
        .message("An error occurred. Please try again.")
        .build();
  }

  private EntityChangeResponse sendErrorResponseWithMessage(String message) {
    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(400))
        .status("fail")
        .message(message)
        .build();
  }
}
