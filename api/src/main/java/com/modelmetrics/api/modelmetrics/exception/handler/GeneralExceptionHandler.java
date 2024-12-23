package com.modelmetrics.api.modelmetrics.exception.handler;

import com.modelmetrics.api.modelmetrics.dto.base.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/** Handles general exceptions not covered by other handlers. */
@RestControllerAdvice
@Order(Integer.MAX_VALUE)
public class GeneralExceptionHandler extends BaseExceptionHandler {

  /**
   * Handles EntityNotFoundException.
   *
   * @param ex the EntityNotFoundException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
    logException("🔍 ENTITY NOT FOUND", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
  }

  /**
   * Handles DataIntegrityViolationException.
   *
   * @param ex the DataIntegrityViolationException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    logException("❌ DATA INTEGRITY VIOLATION", ex.getMessage());

    if (ex.getMessage().contains("Duplicate entry")) {
      return createErrorResponse(
          "Duplicate entry received. Double check unique values and try again.",
          HttpStatus.BAD_REQUEST.value());
    }

    return createErrorResponse(
        "Invalid arguments passed. Please double check and try again.",
        HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles NoHandlerFoundException.
   *
   * @param ex the NoHandlerFoundException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundError(NoHandlerFoundException ex) {
    logException("⛔️ ROUTE NOT FOUND", ex.getMessage());
    String message =
        String.format(
            "The route you requested was not found: %s %s", ex.getHttpMethod(), ex.getRequestURL());
    return createErrorResponse(message, HttpStatus.NOT_FOUND.value());
  }

  /**
   * Handles all other exceptions.
   *
   * @param ex the Exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleError(Exception ex) {
    logException("🔥🔥🔥 UNEXPECTED ERROR 🔥🔥🔥", ex.getMessage());
    return createErrorResponse(
        "Uh oh, something went wrong.... 🤔 Check back later!",
        HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}
