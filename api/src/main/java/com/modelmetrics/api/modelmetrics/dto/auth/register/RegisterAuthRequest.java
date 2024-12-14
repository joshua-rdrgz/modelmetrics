package com.modelmetrics.api.modelmetrics.dto.auth.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** RegisterAuthRequest. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterAuthRequest {

  private String firstName;
  private String lastName;
  private String email;
  private String password;
}
