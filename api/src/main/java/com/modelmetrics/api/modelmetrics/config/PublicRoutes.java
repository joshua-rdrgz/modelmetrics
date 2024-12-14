package com.modelmetrics.api.modelmetrics.config;

import lombok.Getter;

/** PublicRoutes. */
public class PublicRoutes {

  @Getter
  private static String[] whiteList = {
    "/api/v1/auth/register",
    "/api/v1/auth/login",
    "/api/v1/auth/logout",
    "/api/v1/auth/verify",
    "/api/v1/auth/resend-verification",
    "/api/v1/users/email/verify",
    "/api/v1/test/public",
  };
}
