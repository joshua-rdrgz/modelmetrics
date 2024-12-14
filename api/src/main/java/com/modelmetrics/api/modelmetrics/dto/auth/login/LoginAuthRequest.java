package com.modelmetrics.api.modelmetrics.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** LoginAuthRequest. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginAuthRequest {

  private String email;
  private String password;
}
