package com.iam.server.web;

import com.iam.common.api.ApiErrorCode;
import com.iam.common.api.ApiException;
import com.iam.common.api.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 通过@RestControllerAdvice注解，统一处理控制器层抛出的各种异常。
 * 将不同类型的异常转换为统一的ApiResponse格式，并设置相应的HTTP状态码。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理自定义的ApiException异常。
     * 将异常信息转换为统一的ApiResponse格式，并返回400 Bad Request状态码。
     *
     * @param ex 抛出的ApiException实例
     * @return 包含错误代码和消息的ApiResponse对象
     */
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleApiException(final ApiException ex) {
        ApiResponse<Void> resp = ApiResponse.fail(ex.getErrorCode(), ex.getMessage());
        resp.setTraceId(MDC.get("traceId"));
        return resp;
    }

    /**
     * 处理参数校验相关的异常，如请求体校验失败、表单绑定错误等。
     * 统一返回400 Bad Request状态码和预设的校验错误响应。
     *
     * @param ex 抛出的校验异常实例
     * @return 包含校验错误信息的ApiResponse对象
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(final Exception ex) {
        ApiResponse<Void> resp = ApiResponse.fail(ApiErrorCode.VALIDATION_ERROR, ApiErrorCode.VALIDATION_ERROR.defaultMessage());
        resp.setTraceId(MDC.get("traceId"));
        return resp;
    }

    /**
     * 处理身份认证失败的异常（例如用户名密码错误）。
     * 返回401 Unauthorized状态码和未授权的错误响应。
     *
     * @param ex 抛出的AuthenticationException实例
     * @return 包含未授权错误信息的ApiResponse对象
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleAuth(final AuthenticationException ex) {
        ApiResponse<Void> resp = ApiResponse.fail(ApiErrorCode.UNAUTHORIZED, ApiErrorCode.UNAUTHORIZED.defaultMessage());
        resp.setTraceId(MDC.get("traceId"));
        return resp;
    }

    /**
     * 处理权限不足导致的访问被拒绝异常。
     * 当用户通过了认证但没有足够权限访问资源时触发，返回403 Forbidden状态码。
     *
     * @param ex 抛出的AccessDeniedException实例
     * @return 包含禁止访问错误信息的ApiResponse对象
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleDenied(final AccessDeniedException ex) {
        ApiResponse<Void> resp = ApiResponse.fail(ApiErrorCode.FORBIDDEN, ApiErrorCode.FORBIDDEN.defaultMessage());
        resp.setTraceId(MDC.get("traceId"));
        return resp;
    }

    /**
     * 处理所有未被其他处理器捕获的未知异常（兜底处理）。
     * 记录错误并返回500 Internal Server Error状态码和内部错误响应。
     *
     * @param ex 抛出的未预期的Exception实例
     * @return 包含内部错误信息的ApiResponse对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleOther(final Exception ex) {
        ApiResponse<Void> resp = ApiResponse.fail(ApiErrorCode.INTERNAL_ERROR, ApiErrorCode.INTERNAL_ERROR.defaultMessage());
        resp.setTraceId(MDC.get("traceId"));
        return resp;
    }
}

