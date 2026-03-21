package com.iam.server.bootstrap;

import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

/**
 * OAuth2客户端注册初始化器
 * 在应用启动时，为开发和测试环境自动注册一个预定义的OAuth2客户端（vue-client）。
 * 该组件仅在'dev', 'dev-noredis', 'test' Profile下激活。
 */
@Component
@Profile({"dev", "dev-noredis", "test"})
public class RegisteredClientInitializer implements CommandLineRunner {
  private final RegisteredClientRepository registeredClientRepository;
  private final TokenSettings tokenSettings;
  private final ClientSettings clientSettings;

    /**
   * 构造函数，用于注入OAuth2客户端注册所需的服务组件。
   *
   * @param registeredClientRepository 客户端信息持久化仓库
   * @param tokenSettings 令牌生成与管理的配置
   * @param clientSettings 客户端自身的安全与行为配置
   */
  public RegisteredClientInitializer(final RegisteredClientRepository registeredClientRepository,
                                     final TokenSettings tokenSettings,
                                     final ClientSettings clientSettings) {
    this.registeredClientRepository = registeredClientRepository;
    this.tokenSettings = tokenSettings;
    this.clientSettings = clientSettings;
  }

    /**
   * 实现CommandLineRunner接口的方法，在应用上下文加载完成后执行。
   * 负责检查并创建预定义的OAuth2客户端"vue-client"。
   *
   * @param args 命令行参数（本实现中未使用）
   */
  @Override
  public void run(final String... args) {
    // 检查是否已存在ID为"vue-client"的注册客户端，若存在则无需重复创建
    RegisteredClient existing = registeredClientRepository.findByClientId("vue-client");
    if (existing != null) {
      return;
    }

    // 构建一个新的OAuth2客户端配置，包含客户端ID、名称、认证方式、授权类型、重定向URI和作用域等信息
    RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("vue-client")
        .clientName("Vue Admin")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .redirectUri("http://localhost:5173/oauth/callback")
        .redirectUri("http://localhost:5174/oauth/callback")
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope("rbac")
        .tokenSettings(tokenSettings)
        .clientSettings(clientSettings)
        .build();
    registeredClientRepository.save(client);
  }
}

