package com.modelmetrics.api.modelmetrics.service.auth;

import com.modelmetrics.api.modelmetrics.dto.auth.login.LoginAuthRequest;
import com.modelmetrics.api.modelmetrics.dto.auth.register.RegisterAuthRequest;
import com.modelmetrics.api.modelmetrics.dto.response.EntityChangeResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/** AuthService. */
public interface AuthService {

  EntityChangeResponse register(RegisterAuthRequest entity);

  EntityChangeResponse login(LoginAuthRequest entity, HttpServletResponse response);

  EntityChangeResponse logout(HttpServletResponse response);

  EntityChangeResponse verifyAccount(String token, UUID userId);

  EntityChangeResponse resendVerificationEmail(String email);
}
