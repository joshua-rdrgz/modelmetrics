package com.modelmetrics.api.modelmetrics.service.auth;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;

/** JwtService. */
public interface JwtService {

  public String extractEmail(String token);

  public Date extractExpiration(String token);

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

  public Boolean validateToken(String token, UserDetails userDetails);

  public String generateToken(String email);
}
