package com.modelmetrics.api.modelmetrics.dto.password;

import lombok.Data;
import lombok.NoArgsConstructor;

/** PasswordResetVerifyRequest. */
@Data
@NoArgsConstructor
public class PasswordResetVerifyRequest {
  private String otp;
}
