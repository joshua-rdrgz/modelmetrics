package com.modelmetrics.api.modelmetrics.exception;

import com.modelmetrics.api.modelmetrics.dto.base.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/** GlobalErrorHandler. */
@RestControllerAdvice
public class GlobalErrorHandler {

  @ExceptionHandler(UnauthorizedSessionAccessException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedSessionAccess(
      UnauthorizedSessionAccessException ex) {
    ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles validation exceptions thrown by @Valid annotations.
   *
   * @param ex the MethodArgumentNotValidException thrown when validation fails
   * @return ResponseEntity containing ErrorResponse with validation error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().stream()
        .sorted(
            (error1, error2) -> {
              String field1 = ((FieldError) error1).getField();
              String field2 = ((FieldError) error2).getField();

              // Primary comparison: field names
              int fieldComparison = field1.compareTo(field2);
              if (fieldComparison != 0) {
                return fieldComparison;
              }

              // Secondary comparison: error messages
              return error1.getDefaultMessage().compareTo(error2.getDefaultMessage());
            })
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.merge(
                  fieldName, errorMessage, (existing, newError) -> existing + ", " + newError);
            });

    String errorMessage = "Validation failed. Please check the errors field for details.";
    System.out.println("🚫 VALIDATION ERROR: " + errors);

    ErrorResponse error = new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), errors);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles exceptions thrown when the request body contains invalid JSON.
   *
   * @param ex the HttpMessageNotReadableException thrown when JSON parsing fails
   * @return ResponseEntity containing ErrorResponse with details about the JSON parsing error
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {

    Map<String, String> errors = new HashMap<>();
    Throwable mostSpecificCause = ex.getMostSpecificCause();
    errors.put("exception", ex.getClass().getSimpleName());
    errors.put(
        "details", mostSpecificCause != null ? mostSpecificCause.getMessage() : ex.getMessage());

    System.out.println("❌ INVALID JSON: " + errors.get("details"));

    String userFriendlyMessage =
        "The request contains invalid JSON. Please check your request body.";

    ErrorResponse error =
        new ErrorResponse(userFriendlyMessage, HttpStatus.BAD_REQUEST.value(), errors);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

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
