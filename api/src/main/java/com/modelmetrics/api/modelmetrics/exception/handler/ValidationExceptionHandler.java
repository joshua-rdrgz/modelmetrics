package com.modelmetrics.api.modelmetrics.exception.handler;

import com.modelmetrics.api.modelmetrics.dto.base.ErrorResponse;
import com.modelmetrics.api.modelmetrics.exception.InvalidInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Handles validation-related exceptions. */
@RestControllerAdvice
@Order(3)
public class ValidationExceptionHandler extends BaseExceptionHandler {

  /**
   * Handles MethodArgumentNotValidException.
   *
   * @param ex the MethodArgumentNotValidException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, Object> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().stream()
        .sorted(
            (error1, error2) -> {
              String field1 = ((FieldError) error1).getField();
              String field2 = ((FieldError) error2).getField();
              int fieldComparison = field1.compareTo(field2);
              if (fieldComparison != 0) {
                return fieldComparison;
              }
              return error1.getDefaultMessage().compareTo(error2.getDefaultMessage());
            })
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.compute(
                  fieldName,
                  (key, value) -> {
                    if (value instanceof List) {
                      List<String> list = safeCastToList(value);
                      list.add(errorMessage);
                      return list;
                    } else if (value instanceof String) {
                      List<String> list = new ArrayList<>();
                      list.add((String) value);
                      list.add(errorMessage);
                      return list;
                    } else {
                      return errorMessage;
                    }
                  });
            });

    String errorMessage = "Validation failed. Please check the errors field for details.";
    logException("🚫 VALIDATION ERROR", errors.toString());

    ErrorResponse error = new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), errors);
    return ResponseEntity.badRequest().body(error);
  }

  /**
   * Handles HttpMessageNotReadableException.
   *
   * @param ex the HttpMessageNotReadableException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
    Map<String, Object> errors = new HashMap<>();
    Throwable mostSpecificCause = ex.getMostSpecificCause();
    errors.put("exception", ex.getClass().getSimpleName());
    errors.put(
        "details", mostSpecificCause != null ? mostSpecificCause.getMessage() : ex.getMessage());

    logException("❌ INVALID JSON", errors.get("details").toString());

    String userFriendlyMessage =
        "The request contains invalid JSON. Please check your request body.";

    ErrorResponse error =
        new ErrorResponse(userFriendlyMessage, HttpStatus.BAD_REQUEST.value(), errors);
    return ResponseEntity.badRequest().body(error);
  }

  /**
   * Handles IllegalArgumentException.
   *
   * @param ex the IllegalArgumentException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArguments(IllegalArgumentException ex) {
    logException("👮🏽‍♀️ ILLEGAL ARGUMENT EXCEPTION", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Handles InvalidInputException.
   *
   * @param ex the InvalidInputException
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(InvalidInputException.class)
  public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidInputException ex) {
    logException("❌ INVALID INPUT", ex.getMessage());
    return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
  }

  /**
   * Safely casts an object to a list of strings.
   *
   * @param value the object to cast
   * @return the casted list of strings
   * @throws ClassCastException if the object is not a list of strings
   */
  @SuppressWarnings("unchecked")
  private List<String> safeCastToList(Object value) {
    if (value instanceof List<?> list && list.stream().allMatch(item -> item instanceof String)) {
      return (List<String>) value;
    }
    throw new ClassCastException("Value is not a List<String>");
  }
}
