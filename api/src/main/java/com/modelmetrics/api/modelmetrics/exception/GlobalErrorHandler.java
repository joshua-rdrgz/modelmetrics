package com.modelmetrics.api.modelmetrics.exception;

import com.modelmetrics.api.modelmetrics.dto.base.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/** GlobalErrorHandler. */
@RestControllerAdvice
public class GlobalErrorHandler {

  /**
   * handleIllegalArguments.
   *
   * @param ex IllegalArgumentException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleIllegalArguments(IllegalArgumentException ex) {
    System.out.println("👮🏽‍♀️ ILLEGAL ARGUMENT EXCEPTION: " + ex.getMessage());

    ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleCouldNotVerifyUser.
   *
   * @param ex CouldNotVerifyUserException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleCouldNotVerifyUser(CouldNotVerifyUserException ex) {
    System.out.println("❌ COULD NOT VERIFY USER: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            "Could not verify user, please try again later!", HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleUnverifiedUser.
   *
   * @param ex UnverifiedUserException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleUnverifiedUser(UnverifiedUserException ex) {
    System.out.println("🛑 UNVERIFIED USER: " + ex.getMessage());

    ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleUserAlreadyVerified.
   *
   * @param ex UserAlreadyVerifiedException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleUserAlreadyVerified(UserAlreadyVerifiedException ex) {
    System.out.println("⚠️ USER ALREADY VERIFIED: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            "Could not process request, please try again later.", HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleUserNotFound.
   *
   * @param ex UserNotFoundException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    System.out.println("🚫 USER NOT FOUND: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            "Could not process request, please try again later.", HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleBadCredentials.
   *
   * @param ex BadCredentialsException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
    System.out.println("❌ BAD CREDENTIALS: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            "Invalid or missing email/password, please try again.", HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleInvalidInput.
   *
   * @param ex InvalidInputException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidInputException ex) {
    System.out.println("❌ INVALID INPUT: " + ex.getMessage());

    ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleUserAlreadyExists.
   *
   * @param ex UserAlreadyExistsException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    System.out.println("💨 USER ALREADY EXISTS: " + ex.getMessage());

    ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleExpiredJwt.
   *
   * @param ex ExpiredJwtException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
    System.out.println("🗑️ JWT EXPIRED: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            "Access Denied: Please log in and try again.", HttpStatus.UNAUTHORIZED.value());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  /**
   * handleInvalidJwt.
   *
   * @param ex SignatureException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleInvalidJwt(SignatureException ex) {
    System.out.println("🚑 JWT INVALID: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            "Access Denied: Please log in and try again.", HttpStatus.UNAUTHORIZED.value());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  /**
   * handleAccessDenied.
   *
   * @param ex AccessDeniedException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
    System.out.println("🛑 ACCESS DENIED: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            "Access Denied: Please log in to gain access.", HttpStatus.UNAUTHORIZED.value());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  /**
   * handleDataIntegrityViolation.
   *
   * @param ex DataIntegrityViolationException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    System.out.println("❌ DATA INTEGRITY VIOLATION: " + ex.getMessage());

    if (ex.getMessage().contains("Duplicate entry")) {
      ErrorResponse error =
          new ErrorResponse(
              "Duplicate entry received.  Double check unique values and try again.",
              HttpStatus.BAD_REQUEST.value());
      return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    ErrorResponse error =
        new ErrorResponse(
            "Invalid arguments passed.  Please double check and try again.",
            HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * handleNotFoundError.
   *
   * @param ex NoHandlerFoundException
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleNotFoundError(NoHandlerFoundException ex) {
    System.out.println("⛔️ ROUTE NOT FOUND: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            String.format(
                "The route you requested was not found: %s %s",
                ex.getHttpMethod(), ex.getRequestURL()),
            HttpStatus.NOT_FOUND.value());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  /**
   * handleError.
   *
   * @param ex Exception
   * @return ResponseEntity
   */
  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleError(Exception ex) {
    System.out.println("🔥🔥🔥 UNEXPECTED ERROR 🔥🔥🔥: " + ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            "Uh oh, something went wrong.... 🤔  Check back later!",
            HttpStatus.INTERNAL_SERVER_ERROR.value());

    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
