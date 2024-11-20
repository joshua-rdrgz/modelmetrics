package com.dataannotationlogs.api.dalogs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dataannotationlogs.api.dalogs.base.EmailTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modelmetrics.api.modelmetrics.dto.auth.login.LoginAuthRequest;
import com.modelmetrics.api.modelmetrics.dto.auth.register.RegisterAuthRequest;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.VerificationToken;
import com.modelmetrics.api.modelmetrics.repository.user.UserRepository;
import com.modelmetrics.api.modelmetrics.repository.verificationtoken.VerificationTokenRepository;
import com.modelmetrics.api.modelmetrics.service.auth.JwtService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** AuthControllerTest. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class AuthControllerTest extends EmailTestBase {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private VerificationTokenRepository verificationTokenRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtService jwtService;

  @Value("${security.jwt.cookie-token-key}")
  private String tokenKey;

  private static final String UNVERIFIED_EMAIL = "john@example.com";
  private static final String VERIFIED_EMAIL = "verified@example.com";
  private static final String TEST_PASSWORD = "password123";

  private RegisterAuthRequest validRegistrationRequest;
  private LoginAuthRequest validLoginRequest;
  private User verifiedUser;
  private User unverifiedUser;

  /** setup. */
  @BeforeEach
  public void setup() {
    validRegistrationRequest =
        RegisterAuthRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email(UNVERIFIED_EMAIL)
            .password(TEST_PASSWORD)
            .build();

    validLoginRequest =
        LoginAuthRequest.builder().email(VERIFIED_EMAIL).password(TEST_PASSWORD).build();

    verifiedUser = createUser(VERIFIED_EMAIL, true);
    unverifiedUser = createUser(UNVERIFIED_EMAIL, false);
  }

  private User createUser(String email, boolean verified) {
    return userRepository.save(
        User.builder()
            .email(email)
            .password(passwordEncoder.encode(TEST_PASSWORD))
            .verified(verified)
            .build());
  }

  private Cookie createAuthCookie(String email) {
    String token = jwtService.generateToken(email);
    return new Cookie(tokenKey, token);
  }

  @Test
  public void registerUser_withValidData_shouldCreateUser() throws Exception {
    // Test: Register a new user with valid data
    // Expected: User should be created successfully
    String newEmail = "newuser@example.com";
    RegisterAuthRequest newUserRequest =
        RegisterAuthRequest.builder()
            .firstName("New")
            .lastName("User")
            .email(newEmail)
            .password(TEST_PASSWORD)
            .build();

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUserRequest)))
        .andExpect(status().isOk());

    // Verify that the email was sent
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(1, receivedMessages.length);
    assertEquals(newEmail, receivedMessages[0].getAllRecipients()[0].toString());
  }

  @Test
  public void registerUser_withExistingEmail_shouldReturnError() throws Exception {
    // Test: Register a new user with an existing email
    // Expected: User creation should fail with an appropriate error message
    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void loginUser_withValidCredentials_shouldLoginSuccessfully() throws Exception {
    // Test: Login with valid credentials
    // Expected: User should be logged in successfully
    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
        .andExpect(status().isOk());
  }

  @Test
  public void loginUser_withInvalidCredentials_shouldReturnError() throws Exception {
    // Test: Login with invalid credentials
    // Expected: Login should fail with an appropriate error message
    LoginAuthRequest invalidRequest =
        LoginAuthRequest.builder().email(VERIFIED_EMAIL).password("wrongpassword").build();

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void loginUser_withUnverifiedAccount_shouldReturnError() throws Exception {
    // Test: Login with an unverified account
    // Expected: Login should fail with an appropriate error message
    LoginAuthRequest unverifiedLoginRequest =
        LoginAuthRequest.builder().email(UNVERIFIED_EMAIL).password(TEST_PASSWORD).build();

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unverifiedLoginRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void logoutUser_withValidToken_shouldLogoutSuccessfully() throws Exception {
    // Test: Logout with a valid token
    // Expected: User should be logged out successfully
    mockMvc
        .perform(get("/api/v1/auth/logout").cookie(createAuthCookie(verifiedUser.getEmail())))
        .andExpect(status().isOk())
        .andExpect(cookie().maxAge(tokenKey, 0));
  }

  @Test
  public void logoutUser_withoutToken_shouldLogoutSuccessfully() throws Exception {
    // Test: Logout without a token
    // Expected: Logout should still succeed
    mockMvc
        .perform(get("/api/v1/auth/logout"))
        .andExpect(status().isOk())
        .andExpect(cookie().maxAge(tokenKey, 0));
  }

  @Test
  public void logoutUser_multipleTimes_shouldLogoutSuccessfully() throws Exception {
    // Test: Logout multiple times
    // Expected: Each logout should succeed
    Cookie authCookie = createAuthCookie(verifiedUser.getEmail());

    for (int i = 0; i < 3; i++) {
      mockMvc
          .perform(get("/api/v1/auth/logout").cookie(authCookie))
          .andExpect(status().isOk())
          .andExpect(cookie().maxAge(tokenKey, 0));
    }
  }

  @Test
  public void verifyAccount_withValidToken_shouldVerifyAccount() throws Exception {
    // Test: Verify an account with a valid token
    // Expected: Account should be verified successfully
    String token = UUID.randomUUID().toString();

    verificationTokenRepository.save(
        VerificationToken.builder()
            .userId(unverifiedUser.getId())
            .user(unverifiedUser)
            .token(passwordEncoder.encode(token))
            .expiryDate(LocalDateTime.now().plusHours(24))
            .build());

    mockMvc
        .perform(
            post("/api/v1/auth/verify")
                .param("token", token)
                .param("userId", unverifiedUser.getId().toString()))
        .andExpect(status().isOk());
  }

  @Test
  public void verifyAccount_withInvalidToken_shouldReturnError() throws Exception {
    // Test: Verify an account with an invalid token
    // Expected: Account verification should fail with an appropriate error message
    mockMvc
        .perform(
            post("/api/v1/auth/verify")
                .param("token", "invalid_token")
                .param("userId", unverifiedUser.getId().toString()))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void resendVerificationEmail_withValidEmail_shouldSendEmail() throws Exception {
    // Test: Resend verification email with a valid email
    // Expected: Verification email should be sent successfully
    mockMvc
        .perform(post("/api/v1/auth/resend-verification").param("email", UNVERIFIED_EMAIL))
        .andExpect(status().isOk());

    // Verify that the email was sent
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(1, receivedMessages.length);
    assertEquals(UNVERIFIED_EMAIL, receivedMessages[0].getAllRecipients()[0].toString());
  }
}
