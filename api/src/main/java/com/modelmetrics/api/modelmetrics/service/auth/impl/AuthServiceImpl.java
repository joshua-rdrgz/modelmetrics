package com.modelmetrics.api.modelmetrics.service.auth.impl;

import com.modelmetrics.api.modelmetrics.dto.auth.login.LoginAuthRequest;
import com.modelmetrics.api.modelmetrics.dto.auth.register.RegisterAuthRequest;
import com.modelmetrics.api.modelmetrics.dto.response.EntityChangeResponse;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.VerificationToken;
import com.modelmetrics.api.modelmetrics.exception.CouldNotVerifyUserException;
import com.modelmetrics.api.modelmetrics.exception.InvalidInputException;
import com.modelmetrics.api.modelmetrics.exception.UnverifiedUserException;
import com.modelmetrics.api.modelmetrics.exception.UserAlreadyExistsException;
import com.modelmetrics.api.modelmetrics.repository.user.UserRepository;
import com.modelmetrics.api.modelmetrics.repository.verificationtoken.VerificationTokenRepository;
import com.modelmetrics.api.modelmetrics.service.auth.AuthService;
import com.modelmetrics.api.modelmetrics.service.auth.JwtService;
import com.modelmetrics.api.modelmetrics.service.email.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** AuthServiceImpl. */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final AuthenticationManager authenticationManager;

  @Value("${security.jwt.cookie-token-key}")
  private String tokenKey;

  @Value("${security.jwt.expiration-time}")
  private String jwtExpirationTime;

  @Override
  public EntityChangeResponse register(RegisterAuthRequest entity) {

    if (entity.getEmail() == null || entity.getEmail().length() < 5) {
      throw new InvalidInputException("Email must be present.  Please add one.");
    }

    if (entity.getPassword() == null || entity.getPassword().length() < 8) {
      throw new InvalidInputException("Password must be at least 8 characters.");
    }

    var potentialUser = userRepository.findByEmail(entity.getEmail());
    if (potentialUser != null) {
      throw new UserAlreadyExistsException(
          "This email is already taken.  Please choose another one.");
    }

    // 1. Create User
    var user =
        User.builder()
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .email(entity.getEmail())
            .password(passwordEncoder.encode(entity.getPassword()))
            .verified(false)
            .build();
    userRepository.save(user);

    // 2. Create User's Verification Token
    Pair<VerificationToken, String> verificationTokenPair = createVerificationToken(user);
    VerificationToken verificationToken = verificationTokenPair.getFirst();
    String token = verificationTokenPair.getSecond();

    verificationTokenRepository.save(verificationToken);

    // 3. Send User Verification Email
    emailService.sendAccountVerificationEmail(user, token);

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(201))
        .status("success")
        .message("User created, please verify account!")
        .build();
  }

  @Override
  public EntityChangeResponse login(LoginAuthRequest entity, HttpServletResponse response) {
    // 1. Authenticate User
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(entity.getEmail(), entity.getPassword()));

    // 2. Check if User is Verified
    var user = userRepository.findByEmail(entity.getEmail());

    if (!user.isVerified()) {
      throw new UnverifiedUserException("User is not verified. Please verify your account first.");
    }

    // 3. Log User In
    var jwtToken = jwtService.generateToken(user.getEmail());

    ResponseCookie cookie =
        ResponseCookie.from(tokenKey, jwtToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(Integer.parseInt(jwtExpirationTime))
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("Successfully logged in.")
        .build();
  }

  @Override
  public EntityChangeResponse logout(HttpServletResponse response) {
    ResponseCookie cookie =
        ResponseCookie.from(tokenKey, null)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    SecurityContextHolder.clearContext();

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("Successfully logged out.")
        .build();
  }

  @Override
  public EntityChangeResponse verifyAccount(String token, UUID userId) {
    VerificationToken verificationToken = verificationTokenRepository.findByUserId(userId);

    if (verificationToken == null
        || !verificationToken.getUserId().equals(userId)
        || !passwordEncoder.matches(token, verificationToken.getToken())
        || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new CouldNotVerifyUserException("Invalid or expired verification token.");
    }

    userRepository.verifyUser(userId);

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message("Account verified successfully.")
        .build();
  }

  @Override
  public EntityChangeResponse resendVerificationEmail(String email) {
    User user = userRepository.findByEmail(email);

    if (user != null && !user.isVerified()) {
      VerificationToken existingToken = verificationTokenRepository.findByUserId(user.getId());

      if (existingToken != null) {
        verificationTokenRepository.delete(existingToken);
      }

      Pair<VerificationToken, String> verificationTokenPair = createVerificationToken(user);
      VerificationToken verificationToken = verificationTokenPair.getFirst();
      String token = verificationTokenPair.getSecond();
      verificationTokenRepository.save(verificationToken);

      emailService.sendAccountVerificationEmail(user, token);
    }

    return EntityChangeResponse.builder()
        .statusCode(HttpStatusCode.valueOf(200))
        .status("success")
        .message(
            "If the email is associated with an unverified account, a verification link has been"
                + " sent.")
        .build();
  }

  private Pair<VerificationToken, String> createVerificationToken(User user) {
    String token = UUID.randomUUID().toString();
    LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
    VerificationToken verificationToken =
        VerificationToken.builder()
            .userId(user.getId())
            .user(user)
            .token(passwordEncoder.encode(token))
            .expiryDate(expiryDate)
            .build();

    return Pair.of(verificationToken, token);
  }
}
