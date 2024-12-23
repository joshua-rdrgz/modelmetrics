package com.modelmetrics.api.modelmetrics.exception.handler;

import com.modelmetrics.api.modelmetrics.dto.base.ErrorResponse;
import org.springframework.http.ResponseEntity;

/** Abstract base class for exception handlers providing common functionality. */
public abstract class BaseExceptionHandler {

  /**
   * Creates a ResponseEntity with an ErrorResponse.
   *
   * @param message the error message
   * @param statusCode the HTTP status code
   * @return a ResponseEntity with an ErrorResponse
   */
  protected ResponseEntity<ErrorResponse> createErrorResponse(String message, int statusCode) {
    ErrorResponse error = new ErrorResponse(message, statusCode);
    return ResponseEntity.status(statusCode).body(error);
  }

  /**
   * Logs the exception message with a prefix.
   *
   * @param prefix the log prefix
   * @param message the exception message
   */
  protected void logException(String prefix, String message) {
    System.out.println(prefix + ": " + message);
  }
}
