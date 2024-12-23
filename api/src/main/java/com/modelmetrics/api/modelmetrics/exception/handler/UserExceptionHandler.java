package com.modelmetrics.api.modelmetrics.exception.handler;

import com.modelmetrics.api.modelmetrics.dto.base.ErrorResponse;
import com.modelmetrics.api.modelmetrics.exception.CouldNotVerifyUserException;
import com.modelmetrics.api.modelmetrics.exception.UnverifiedUserException;
import com.modelmetrics.api.modelmetrics.exception.UserAlreadyExistsException;
import com.modelmetrics.api.modelmetrics.exception.UserAlreadyVerifiedException;
import com.modelmetrics.api.modelmetrics.exception.UserNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Handles user-related exceptions. */
@RestControllerAdvice
@Order(2)
public class UserExceptionHandler extends BaseExceptionHandler {

  /**
   * Handles UserNotFoundException.
   *
   * @param ex the UserNotFoundException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    logException("🚫 USER NOT FOUND", ex.getMessage());
    return createErrorResponse(
        "Could not process request, please try again later.", HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles UserAlreadyExistsException.
   *
   * @param ex the UserAlreadyExistsException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    logException("💨 USER ALREADY EXISTS", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles UserAlreadyVerifiedException.
   *
   * @param ex the UserAlreadyVerifiedException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(UserAlreadyVerifiedException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyVerified(UserAlreadyVerifiedException ex) {
    logException("⚠️ USER ALREADY VERIFIED", ex.getMessage());
    return createErrorResponse(
        "Could not process request, please try again later.", HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles CouldNotVerifyUserException.
   *
   * @param ex the CouldNotVerifyUserException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(CouldNotVerifyUserException.class)
  public ResponseEntity<ErrorResponse> handleCouldNotVerifyUser(CouldNotVerifyUserException ex) {
    logException("❌ COULD NOT VERIFY USER", ex.getMessage());
    return createErrorResponse(
        "Could not verify user, please try again later!", HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles UnverifiedUserException.
   *
   * @param ex the UnverifiedUserException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(UnverifiedUserException.class)
  public ResponseEntity<ErrorResponse> handleUnverifiedUser(UnverifiedUserException ex) {
    logException("🛑 UNVERIFIED USER", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
  }
}
