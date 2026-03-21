package com.iam.common.api;

public class ApiException extends RuntimeException {
  private final ApiErrorCode errorCode;

  public ApiException(final ApiErrorCode errorCode, final String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public ApiException(final ApiErrorCode errorCode) {
    super(errorCode.defaultMessage());
    this.errorCode = errorCode;
  }

  public ApiErrorCode getErrorCode() {
    return errorCode;
  }
}

