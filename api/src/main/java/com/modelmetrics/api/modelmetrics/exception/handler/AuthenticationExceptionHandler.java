package com.modelmetrics.api.modelmetrics.exception.handler;

import com.modelmetrics.api.modelmetrics.dto.base.ErrorResponse;
import com.modelmetrics.api.modelmetrics.exception.UnauthorizedSessionAccessException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Handles authentication and authorization related exceptions. */
@RestControllerAdvice
@Order(1)
public class AuthenticationExceptionHandler extends BaseExceptionHandler {

  /**
   * Handles UnauthorizedSessionAccessException.
   *
   * @param ex the UnauthorizedSessionAccessException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(UnauthorizedSessionAccessException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedSessionAccess(
      UnauthorizedSessionAccessException ex) {
    logException("🚫 UNAUTHORIZED SESSION ACCESS", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
  }

  /**
   * Handles BadCredentialsException.
   *
   * @param ex the BadCredentialsException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
    logException("❌ BAD CREDENTIALS", ex.getMessage());
    return createErrorResponse(
        "Invalid or missing email/password, please try again.", HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles ExpiredJwtException.
   *
   * @param ex the ExpiredJwtException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
    logException("🗑️ JWT EXPIRED", ex.getMessage());
    return createErrorResponse(
        "Access Denied: Please log in and try again.", HttpStatus.UNAUTHORIZED.value());
  }

  /**
   * Handles SignatureException.
   *
   * @param ex the SignatureException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJwt(SignatureException ex) {
    logException("🚑 JWT INVALID", ex.getMessage());
    return createErrorResponse(
        "Access Denied: Please log in and try again.", HttpStatus.UNAUTHORIZED.value());
  }

  /**
   * Handles AccessDeniedException.
   *
   * @param ex the AccessDeniedException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
    logException("🛑 ACCESS DENIED", ex.getMessage());
    return createErrorResponse(
        "Access Denied: Please log in to gain access.", HttpStatus.UNAUTHORIZED.value());
  }
}
