package com.dataannotationlogs.api.dalogs.controller;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dataannotationlogs.api.dalogs.base.AuthTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelmetrics.api.modelmetrics.dto.password.PasswordChangeRequest;
import com.modelmetrics.api.modelmetrics.dto.password.PasswordResetVerifyRequest;
import com.modelmetrics.api.modelmetrics.dto.response.EntityChangeResponse;
import com.modelmetrics.api.modelmetrics.entity.PasswordResetOtp;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.repository.passwordresetotp.PasswordResetOtpRepository;
import com.modelmetrics.api.modelmetrics.repository.user.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

/** UserControllerPasswordChangeTest. */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerPasswordChangeTest extends AuthTestBase {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordResetOtpRepository passwordResetOtpRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  void sendPasswordResetOtp_whenLoggedIn_shouldSendOtp() throws Exception {
    MvcResult result =
        mockMvc
            .perform(put("/api/v1/users/me/password").cookie(getTokenCookie()))
            .andExpect(status().isOk())
            .andReturn();

    EntityChangeResponse response =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), EntityChangeResponse.class);
    assertEquals("success", response.getStatus());

    PasswordResetOtp otp = passwordResetOtpRepository.findByUserId(getUser().getId());
    assertNotNull(otp);
    assertEquals(1, greenMail.getReceivedMessages().length);
  }

  @Test
  void sendPasswordResetOtp_whenOtpAlreadyExists_shouldFail() throws Exception {
    // Create an existing OTP
    PasswordResetOtp existingOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().plusMinutes(15))
            .build();
    passwordResetOtpRepository.save(existingOtp);

    mockMvc
        .perform(put("/api/v1/users/me/password").cookie(getTokenCookie()))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void sendPasswordResetOtp_whenCooldownActive_shouldReturnRemainingTime() throws Exception {
    int cooldownMinutes = 30;
    PasswordResetOtp existingOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().minusMinutes(1))
            .cooldownComplete(LocalDateTime.now().plusMinutes(cooldownMinutes))
            .build();
    passwordResetOtpRepository.save(existingOtp);

    MvcResult result =
        mockMvc
            .perform(put("/api/v1/users/me/password").cookie(getTokenCookie()))
            .andExpect(status().isBadRequest())
            .andReturn();

    EntityChangeResponse response =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), EntityChangeResponse.class);
    assertTrue(response.getMessage().contains(String.valueOf(cooldownMinutes - 1)));
  }

  @Test
  void sendPasswordResetOtp_whenExistingOtpExpired_shouldSendNewOtp() throws Exception {
    // Create an expired OTP
    PasswordResetOtp expiredOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().minusMinutes(1))
            .build();
    passwordResetOtpRepository.save(expiredOtp);

    mockMvc
        .perform(put("/api/v1/users/me/password").cookie(getTokenCookie()))
        .andExpect(status().isOk())
        .andReturn();

    PasswordResetOtp newOtp = passwordResetOtpRepository.findByUserId(getUser().getId());
    assertNotNull(newOtp);
    assertNotEquals(expiredOtp.getOtp(), newOtp.getOtp());
  }

  @Test
  void sendPasswordResetOtp_whenOnCompletionCooldown_shouldFail() throws Exception {
    User user = getUser();
    user.setLastPasswordChange(LocalDateTime.now().minusHours(23)); // Set last password change to
    // 23 hours ago
    userRepository.save(user);

    mockMvc
        .perform(put("/api/v1/users/me/password").cookie(getTokenCookie()))
        .andExpect(status().isBadRequest())
        .andReturn();

    // Verify that no OTP was created
    PasswordResetOtp otp = passwordResetOtpRepository.findByUserId(user.getId());
    assertNull(otp);
  }

  @Test
  void sendPasswordResetOtp_whenCompletionCooldownExpired_shouldSucceed() throws Exception {
    User user = getUser();
    user.setLastPasswordChange(LocalDateTime.now().minusHours(25)); // Set last password change to
    // 25 hours ago
    userRepository.save(user);

    mockMvc
        .perform(put("/api/v1/users/me/password").cookie(getTokenCookie()))
        .andExpect(status().isOk())
        .andReturn();

    PasswordResetOtp otp = passwordResetOtpRepository.findByUserId(user.getId());
    assertNotNull(otp);
  }

  @Test
  void verifyPasswordResetOtp_whenValidOtp_shouldSucceed() throws Exception {
    // Create an existing OTP
    String otpValue = "123456";
    PasswordResetOtp existingOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode(otpValue))
            .expiryDate(LocalDateTime.now().plusMinutes(15))
            .build();
    passwordResetOtpRepository.save(existingOtp);

    PasswordResetVerifyRequest request = new PasswordResetVerifyRequest();
    request.setOtp(otpValue);

    mockMvc
        .perform(
            put("/api/v1/users/me/password/verify")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    PasswordResetOtp updatedOtp = passwordResetOtpRepository.findByUserId(getUser().getId());
    assertTrue(updatedOtp.getVerified());
  }

  @Test
  void verifyPasswordResetOtp_whenInvalidOtp_shouldFail() throws Exception {
    // Create an existing OTP
    PasswordResetOtp existingOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().plusMinutes(15))
            .build();
    passwordResetOtpRepository.save(existingOtp);

    PasswordResetVerifyRequest request = new PasswordResetVerifyRequest();
    request.setOtp("654321");

    mockMvc
        .perform(
            put("/api/v1/users/me/password/verify")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();

    PasswordResetOtp updatedOtp = passwordResetOtpRepository.findByUserId(getUser().getId());
    assertFalse(updatedOtp.getVerified());
  }

  @Test
  void verifyPasswordResetOtp_whenNoExistingOtp_shouldFail() throws Exception {
    PasswordResetVerifyRequest request = new PasswordResetVerifyRequest();
    request.setOtp("123456");

    mockMvc
        .perform(
            put("/api/v1/users/me/password/verify")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void verifyPasswordResetOtp_whenExistingOtpExpired_shouldFail() throws Exception {
    // Create an expired OTP
    PasswordResetOtp expiredOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().minusMinutes(1))
            .build();
    passwordResetOtpRepository.save(expiredOtp);

    PasswordResetVerifyRequest request = new PasswordResetVerifyRequest();
    request.setOtp("123456");

    MvcResult result =
        mockMvc
            .perform(
                put("/api/v1/users/me/password/verify")
                    .cookie(getTokenCookie())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andReturn();

    EntityChangeResponse response =
        objectMapper.readValue(
            result.getResponse().getContentAsString(), EntityChangeResponse.class);
    assertEquals("fail", response.getStatus());
    assertTrue(response.getMessage().contains("OTP is expired"));
  }

  @Test
  void verifyPasswordResetOtp_whenFinalAttemptUsed_shouldActivateCooldown() throws Exception {
    PasswordResetOtp otp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().plusMinutes(15))
            .attemptsRemaining(1)
            .build();
    passwordResetOtpRepository.save(otp);

    PasswordResetVerifyRequest request = new PasswordResetVerifyRequest();
    request.setOtp("wrong-otp");

    mockMvc
        .perform(
            put("/api/v1/users/me/password/verify")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();

    PasswordResetOtp updatedOtp = passwordResetOtpRepository.findByUserId(getUser().getId());
    assertNotNull(updatedOtp.getCooldownComplete());
    assertTrue(updatedOtp.getCooldownComplete().isAfter(LocalDateTime.now()));
  }

  @Test
  void verifyPasswordResetOtp_whenOnCompletionCooldown_shouldFail() throws Exception {
    User user = getUser();
    user.setLastPasswordChange(LocalDateTime.now().minusHours(23)); // Set last password change to
    // 23 hours ago
    userRepository.save(user);

    PasswordResetVerifyRequest request = new PasswordResetVerifyRequest();
    request.setOtp("123456");

    mockMvc
        .perform(
            put("/api/v1/users/me/password/verify")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void changePassword_whenValidOtp_shouldChangePassword() throws Exception {
    PasswordResetOtp verifiedOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().plusMinutes(15))
            .verified(true)
            .build();
    passwordResetOtpRepository.save(verifiedOtp);

    String newPassword = "newPassword123";
    PasswordChangeRequest request = new PasswordChangeRequest();
    request.setNewPassword(newPassword);

    mockMvc
        .perform(
            put("/api/v1/users/me/password/change")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    User updatedUser = userRepository.findFirstById(getUser().getId());
    assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
  }

  @Test
  void changePassword_whenNoOtpExists_shouldFail() throws Exception {
    String newPassword = "newPassword123";
    PasswordChangeRequest request = new PasswordChangeRequest();
    request.setNewPassword(newPassword);

    mockMvc
        .perform(
            put("/api/v1/users/me/password/change")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();

    User updatedUser = userRepository.findFirstById(getUser().getId());
    assertFalse(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
  }

  @Test
  void changePassword_whenOtpNotVerified_shouldFail() throws Exception {
    // Create an unverified OTP
    PasswordResetOtp unverifiedOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().plusMinutes(15))
            .verified(false)
            .build();
    passwordResetOtpRepository.save(unverifiedOtp);

    String newPassword = "newPassword123";
    PasswordChangeRequest request = new PasswordChangeRequest();
    request.setNewPassword(newPassword);

    mockMvc
        .perform(
            put("/api/v1/users/me/password/change")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();

    User updatedUser = userRepository.findFirstById(getUser().getId());
    assertFalse(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
  }

  @Test
  void changePassword_whenOtpExpired_shouldFail() throws Exception {
    // Create an expired OTP
    PasswordResetOtp expiredOtp =
        PasswordResetOtp.builder()
            .user(getUser())
            .otp(passwordEncoder.encode("123456"))
            .expiryDate(LocalDateTime.now().minusMinutes(1))
            .verified(true)
            .build();
    passwordResetOtpRepository.save(expiredOtp);

    String newPassword = "newPassword123";
    PasswordChangeRequest request = new PasswordChangeRequest();
    request.setNewPassword(newPassword);

    mockMvc
        .perform(
            put("/api/v1/users/me/password/change")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();

    User updatedUser = userRepository.findFirstById(getUser().getId());
    assertFalse(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
  }

  @Test
  void changePassword_whenOnCompletionCooldown_shouldFail() throws Exception {
    User user = getUser();
    user.setLastPasswordChange(LocalDateTime.now().minusHours(23)); // Set last password change to
    // 23 hours ago
    userRepository.save(user);

    PasswordChangeRequest request = new PasswordChangeRequest();
    request.setNewPassword("newPassword123");

    mockMvc
        .perform(
            put("/api/v1/users/me/password/change")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();

    User updatedUser = userRepository.findFirstById(user.getId());
    assertNotEquals("newPassword123", updatedUser.getPassword());
  }
}
