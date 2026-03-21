package com.iam.server.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 追踪ID过滤器
 * 用于在每个HTTP请求中注入唯一的追踪ID (Trace ID)。
 * 如果请求头中不存在X-Trace-Id，则生成一个新的UUID作为追踪ID，并将其添加到MDC和响应头中，
 * 以便于日志的全链路追踪。
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {
  public static final String HEADER = "X-Trace-Id";

    @Override
  /*
    在请求处理链的内部执行过滤逻辑。
    从请求头获取或生成追踪ID，将其放入MDC和响应头，并确保在请求结束后清理MDC中的追踪ID。

    @param request 当前HTTP请求对象
   * @param response 当前HTTP响应对象
   * @param filterChain 过滤器链，用于调用下一个过滤器或目标资源
   * @throws ServletException 如果处理过程中发生servlet错误
   * @throws IOException 如果处理过程中发生IO错误
   */
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain) throws ServletException, IOException {
    String traceId = request.getHeader(HEADER);
    if (traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString();
    }
    MDC.put("traceId", traceId);
    response.setHeader(HEADER, traceId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove("traceId");
    }
  }
}

