package com.modelmetrics.api.modelmetrics.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** ErrorResponse. */
@Getter
@Setter
@ToString
public class ErrorResponse extends Response<Void> {

  private final String message;
  private final long timestamp;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<String, String> errors;

  /**
   * ErrorResponse constructor.
   *
   * @param message error message.
   * @param statusCode error status code.
   */
  public ErrorResponse(String message, int statusCode) {
    super("error", statusCode, null);
    this.message = message;
    this.timestamp = System.currentTimeMillis();
  }

  /**
   * ErrorResponse constructor with validation errors.
   *
   * @param message error message.
   * @param statusCode error status code.
   * @param errors map of field-specific validation errors.
   */
  public ErrorResponse(String message, int statusCode, Map<String, String> errors) {
    this(message, statusCode);
    this.errors = errors;
  }
}
