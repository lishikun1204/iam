package com.iam.common.api;

public enum ApiErrorCode {
  OK("0", "OK"),
  VALIDATION_ERROR("VALIDATION_ERROR", "参数校验失败"),
  UNAUTHORIZED("AUTH_UNAUTHORIZED", "未登录或登录已过期"),
  FORBIDDEN("AUTH_FORBIDDEN", "无权限"),
  NOT_FOUND("RESOURCE_NOT_FOUND", "资源不存在"),
  CONFLICT("CONFLICT_DUPLICATE", "资源冲突"),
  INTERNAL_ERROR("INTERNAL_ERROR", "服务器内部错误");

  private final String code;
  private final String defaultMessage;

  ApiErrorCode(final String code, final String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  public String code() {
    return code;
  }

  public String defaultMessage() {
    return defaultMessage;
  }
}

