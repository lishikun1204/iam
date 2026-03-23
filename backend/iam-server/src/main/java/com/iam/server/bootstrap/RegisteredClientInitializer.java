package com.iam.server.bootstrap;

import java.util.UUID;
import java.util.stream.IntStream;
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
@Profile({ "dev", "dev-noredis", "test" })
public class RegisteredClientInitializer implements CommandLineRunner {
  private final RegisteredClientRepository registeredClientRepository;
  private final TokenSettings tokenSettings;
  private final ClientSettings clientSettings;

  /**
   * 构造函数，用于注入OAuth2客户端注册所需的服务组件。
   *
   * @param registeredClientRepository 客户端信息持久化仓库
   * @param tokenSettings              令牌生成与管理的配置
   * @param clientSettings             客户端自身的安全与行为配置
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
    var devRedirectUris = buildDevRedirectUris();
    if (existing != null) {
      boolean needUpdate = devRedirectUris.stream().anyMatch(uri -> !existing.getRedirectUris().contains(uri));
      if (!needUpdate) {
        return;
      }
      var builder = RegisteredClient.from(existing);
      devRedirectUris.forEach(uri -> {
        if (!existing.getRedirectUris().contains(uri)) {
          builder.redirectUri(uri);
        }
      });
      registeredClientRepository.save(builder.build());
      return;
    }

    // 构建一个新的OAuth2客户端配置，包含客户端ID、名称、认证方式、授权类型、重定向URI和作用域等信息
    var builder = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("vue-client")
        .clientName("Vue Admin")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope("rbac")
        .tokenSettings(tokenSettings)
        .clientSettings(clientSettings);
    devRedirectUris.forEach(builder::redirectUri);
    registeredClientRepository.save(builder.build());
  }

  private static java.util.List<String> buildDevRedirectUris() {
    var hosts = java.util.List.of("localhost", "127.0.0.1", "10.0.2.7");
    int fromPort = 5173;
    int toPort = 5185;
    return hosts.stream()
        .flatMap(host -> IntStream.rangeClosed(fromPort, toPort)
            .mapToObj(port -> "http://" + host + ":" + port + "/oauth/callback"))
        .toList();
  }
}
