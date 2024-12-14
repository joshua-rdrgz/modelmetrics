package com.modelmetrics.api.modelmetrics.repository.passwordresetotp;

import com.modelmetrics.api.modelmetrics.entity.PasswordResetOtp;
import java.util.UUID;

/** PasswordResetOtpRepository. */
public interface PasswordResetOtpRepository {
  PasswordResetOtp save(PasswordResetOtp otp);

  PasswordResetOtp findByUserId(UUID userId);

  void delete(PasswordResetOtp otp);
}
