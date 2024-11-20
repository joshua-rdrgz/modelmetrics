package com.modelmetrics.api.modelmetrics.dto.base;

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
}
