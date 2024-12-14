package com.modelmetrics.api.modelmetrics.service.auth.impl;

import com.modelmetrics.api.modelmetrics.service.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/** JwtServiceImpl. */
@Component
public class JwtServiceImpl implements JwtService {

  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.expiration-time}")
  private long jwtExpirationTime;

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String email = extractEmail(token);
    return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public String generateToken(String email) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, email);
  }

  private String createToken(Map<String, Object> claims, String email) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationTime * 1000L);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(expiryDate)
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
