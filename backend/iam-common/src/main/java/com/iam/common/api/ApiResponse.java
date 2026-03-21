package com.iam.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private String code;
  private String msg;
  private T data;
  private long timestamp;
  private String traceId;

  public ApiResponse() {
  }

  public ApiResponse(final String code, final String msg, final T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
    this.timestamp = Instant.now().toEpochMilli();
  }

  public static <T> ApiResponse<T> ok(final T data) {
    return new ApiResponse<>(ApiErrorCode.OK.code(), ApiErrorCode.OK.defaultMessage(), data);
  }

  public static <T> ApiResponse<T> fail(final ApiErrorCode errorCode, final String msg) {
    return new ApiResponse<>(errorCode.code(), msg, null);
  }

  public String getCode() {
    return code;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(final String msg) {
    this.msg = msg;
  }

  public T getData() {
    return data;
  }

  public void setData(final T data) {
    this.data = data;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(final String traceId) {
    this.traceId = traceId;
  }
}

