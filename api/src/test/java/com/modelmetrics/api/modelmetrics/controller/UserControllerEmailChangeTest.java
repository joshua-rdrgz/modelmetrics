package com.modelmetrics.api.modelmetrics.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelmetrics.api.modelmetrics.base.AuthTestBase;
import com.modelmetrics.api.modelmetrics.dto.email.EmailResetRequest;
import com.modelmetrics.api.modelmetrics.dto.email.EmailResetVerificationRequest;
import com.modelmetrics.api.modelmetrics.entity.EmailResetToken;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.repository.emailresettoken.EmailResetTokenRepository;
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

/** UserControllerEmailChangeTest. */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerEmailChangeTest extends AuthTestBase {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private EmailResetTokenRepository emailResetTokenRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  void sendEmailResetToken_whenLoggedIn_shouldSendEmailResetToken() throws Exception {
    EmailResetRequest emailResetRequest = new EmailResetRequest();
    emailResetRequest.setEmail("jane@example.com");

    mockMvc
        .perform(
            put("/api/v1/users/me/email")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailResetRequest)))
        .andExpect(status().isOk())
        .andReturn();

    // Verify that the email was sent
    assertEquals(1, greenMail.getReceivedMessages().length);
  }

  @Test
  void sendEmailResetToken_whenTokenAlreadyExists_shouldFail() throws Exception {
    EmailResetRequest emailResetRequest = new EmailResetRequest();
    emailResetRequest.setEmail("jane@example.com");

    // Manually create an existing email reset token
    EmailResetToken emailResetToken =
        EmailResetToken.builder()
            .user(getUser())
            .token(passwordEncoder.encode("token"))
            .newEmail(emailResetRequest.getEmail())
            .expiryDate(LocalDateTime.now().plusMinutes(30))
            .build();
    emailResetTokenRepository.save(emailResetToken);

    mockMvc
        .perform(
            put("/api/v1/users/me/email")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailResetRequest)))
        .andExpect(status().isBadRequest());

    // Verify that no email was sent
    assertEquals(0, greenMail.getReceivedMessages().length);
  }

  @Test
  void changeEmail_whenInvalidTokenIsProvided_shouldNotChangeEmail() throws Exception {
    EmailResetVerificationRequest emailResetVerificationRequest =
        new EmailResetVerificationRequest();
    emailResetVerificationRequest.setToken("invalid-token");
    emailResetVerificationRequest.setUserId(getUser().getId()); // Set the userId

    mockMvc
        .perform(
            put("/api/v1/users/email/verify")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailResetVerificationRequest)))
        .andExpect(status().isBadRequest());

    // Verify that the email was not changed
    User updatedUser = userRepository.findFirstById(getUser().getId());
    assertEquals(getUser().getEmail(), updatedUser.getEmail());
  }

  @Test
  void changeEmail_whenValidTokenIsProvided_shouldChangeEmail() throws Exception {
    String email = "jane@example.com";

    // Manually create an existing email reset token
    EmailResetToken emailResetToken =
        EmailResetToken.builder()
            .user(getUser())
            .token(passwordEncoder.encode("token"))
            .newEmail(email)
            .expiryDate(LocalDateTime.now().plusMinutes(30))
            .build();
    emailResetTokenRepository.save(emailResetToken);

    EmailResetVerificationRequest emailResetVerificationRequest =
        new EmailResetVerificationRequest();
    emailResetVerificationRequest.setToken("token");
    emailResetVerificationRequest.setUserId(getUser().getId()); // Set the userId

    mockMvc
        .perform(
            put("/api/v1/users/email/verify")
                .cookie(getTokenCookie())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailResetVerificationRequest)))
        .andExpect(status().isOk());

    // Verify that the email was changed
    User updatedUser = userRepository.findFirstById(getUser().getId());
    assertEquals(email, updatedUser.getEmail());
  }

  @Test
  void cancelEmailResetToken_whenEmailResetTokenExists_shouldCancelToken() throws Exception {
    // Create an email reset token for the user
    String newEmail = "newemail@example.com";
    EmailResetToken emailResetToken =
        EmailResetToken.builder()
            .user(getUser())
            .token(passwordEncoder.encode("token"))
            .newEmail(newEmail)
            .expiryDate(LocalDateTime.now().plusMinutes(30))
            .build();
    emailResetTokenRepository.save(emailResetToken);

    // Call the cancelEmailResetToken endpoint
    mockMvc
        .perform(put("/api/v1/users/me/email/cancel-reset").cookie(getTokenCookie()))
        .andExpect(status().isOk());

    // Verify that the email reset token was deleted
    EmailResetToken deletedToken = emailResetTokenRepository.findByUserId(getUser().getId());
    assertNull(deletedToken);
  }

  @Test
  void cancelEmailResetToken_whenNoEmailResetTokenExists_shouldReturnBadRequest() throws Exception {
    // Call the cancelEmailResetToken endpoint
    MvcResult result =
        mockMvc
            .perform(put("/api/v1/users/me/email/cancel-reset").cookie(getTokenCookie()))
            .andExpect(status().isBadRequest())
            .andReturn();

    // Verify the response message
    String responseBody = result.getResponse().getContentAsString();
    assertTrue(responseBody.contains("No email reset token found for the user."));
  }
}
