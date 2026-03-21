package com.iam.server.web;

import com.iam.common.api.ApiErrorCode;
import com.iam.common.api.ApiResponse;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * API响应统一包装切面
 * 实现Spring的ResponseBodyAdvice接口，用于在控制器方法返回后，将响应体自动包装成统一的ApiResponse格式。
 * 仅对com.iam.server.api包下的控制器生效，避免影响静态资源或第三方库的响应。
 */
@Component
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
  /**
   * 判断当前ResponseBodyAdvice是否应应用于给定的控制器方法。
   * 仅当该方法属于com.iam.server.api包下的类时返回true，以确保只包装API响应。
   *
   * @param returnType 控制器方法返回值的类型参数
   * @param converterType 将用于写入响应体的消息转换器类型
   * @return 如果需要应用此advice则返回true，否则返回false
   */
  public boolean supports(@NonNull final MethodParameter returnType,
                          @NonNull final Class<? extends HttpMessageConverter<?>> converterType) {
    String className = returnType.getContainingClass().getName();
    return className.startsWith("com.iam.server.api");
  }

    @Override
  /**
   * 在响应体被写入之前，对其进行处理和包装。
   * 将非ApiResponse类型的返回值包装成ApiResponse对象，并添加跟踪ID(traceId)。
   * 如果返回值已是ApiResponse，则直接返回，不进行二次包装。
   *
   * @param body 原始的响应体对象
   * @param returnType 控制器方法返回值的类型参数
   * @param selectedContentType 所选中的媒体类型
   * @param selectedConverterType 所选中的消息转换器类型
   * @param request 当前HTTP请求
   * @param response 当前HTTP响应
   * @return 经过处理后的响应体对象
   */
  public Object beforeBodyWrite(@Nullable final Object body,
                                @NonNull final MethodParameter returnType,
                                @NonNull final MediaType selectedContentType,
                                @NonNull final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                @NonNull final ServerHttpRequest request,
                                @NonNull final ServerHttpResponse response) {
    if (body instanceof ApiResponse) {
      return body;
    }
    ApiResponse<Object> resp = new ApiResponse<>(ApiErrorCode.OK.code(), ApiErrorCode.OK.defaultMessage(), body);
    resp.setTraceId(MDC.get("traceId"));
    return resp;
  }
}

