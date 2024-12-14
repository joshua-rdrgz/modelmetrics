package com.modelmetrics.api.modelmetrics.dto.base;

/** SuccessResponse. */
public class SuccessResponse<T> extends Response<T> {

  public SuccessResponse(T data, int statusCode) {
    super("success", statusCode, data);
  }
}
