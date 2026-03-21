package com.iam.server.web;

import com.iam.common.api.ApiErrorCode;
import com.iam.common.api.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;

class GlobalExceptionHandlerTest {
  @Test
  void handleApiException_shouldReturnCode() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    ApiException ex = new ApiException(ApiErrorCode.CONFLICT, "dup");
    Assertions.assertEquals(ApiErrorCode.CONFLICT.code(), handler.handleApiException(ex).getCode());
  }

  @Test
  void handleValidation_shouldReturnValidationCode() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Assertions.assertEquals(ApiErrorCode.VALIDATION_ERROR.code(), handler.handleValidation(new BindException(new Object(), "x")).getCode());
  }

  @Test
  void handleAuth_shouldReturnUnauthorizedCode() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Assertions.assertEquals(ApiErrorCode.UNAUTHORIZED.code(), handler.handleAuth(new BadCredentialsException("bad")).getCode());
  }

  @Test
  void handleDenied_shouldReturnForbiddenCode() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Assertions.assertEquals(ApiErrorCode.FORBIDDEN.code(), handler.handleDenied(new AccessDeniedException("no")).getCode());
  }

  @Test
  void handleOther_shouldReturnInternalCode() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Assertions.assertEquals(ApiErrorCode.INTERNAL_ERROR.code(), handler.handleOther(new RuntimeException("x")).getCode());
  }
}
