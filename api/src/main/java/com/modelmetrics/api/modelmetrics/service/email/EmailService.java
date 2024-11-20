package com.modelmetrics.api.modelmetrics.service.email;

import com.modelmetrics.api.modelmetrics.entity.User;
import java.util.UUID;

/** EmailService. */
public interface EmailService {
  void sendAccountVerificationEmail(User user, String token);

  void sendEmailChangeEmail(String email, String token, UUID userId);

  void sendPasswordResetOtpEmail(String email, String otp);

  void sendAccountDeletedEmail(String email);

  void sendAccountVerificationReminderEmail(String email);
}
