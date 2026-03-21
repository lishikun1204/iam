package com.iam.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
/**
 * CORS配置类，用于定义跨域资源共享策略
 * 通过注入IamProperties获取允许的源列表，并配置相关CORS规则
 */
public class CorsConfig {
  @Bean
  @Primary
      /*
      创建并配置CORS配置源

      @param properties 应用程序配置属性，包含安全相关的设置，特别是CORS允许的源列表
     * @return 配置好的CORS资源配置源，适用于所有请求路径(/**)
     */
    public CorsConfigurationSource corsConfigurationSource(final IamProperties properties) {
    CorsConfiguration configuration = new CorsConfiguration();
    // 设置允许的源列表，从应用配置中读取
    configuration.setAllowedOrigins(List.copyOf(properties.getSecurity().getCors().getAllowedOrigins()));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

