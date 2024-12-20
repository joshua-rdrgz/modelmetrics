package com.modelmetrics.api.modelmetrics.base;

import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.repository.user.UserRepository;
import com.modelmetrics.api.modelmetrics.service.auth.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/** AuthTestBase. */
@SpringBootTest
public abstract class AuthTestBase extends EmailTestBase {

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private JwtService jwtService;

  private User user;
  private Cookie tokenCookie;

  @BeforeEach
  void initializeAuth() {
    user =
        User.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .password(passwordEncoder.encode("password"))
            .verified(true)
            .build();
    user = userRepository.save(user);

    String jwtToken = jwtService.generateToken(user.getEmail());
    tokenCookie = new Cookie("testtoken", jwtToken);
  }

  protected User getUser() {
    return user;
  }

  protected Cookie getTokenCookie() {
    return tokenCookie;
  }

  protected User createAndSaveUser(String email) {
    User newUser =
        User.builder()
            .firstName("Test")
            .lastName("User")
            .email(email)
            .password(passwordEncoder.encode("password"))
            .verified(true)
            .build();
    return userRepository.save(newUser);
  }
}
