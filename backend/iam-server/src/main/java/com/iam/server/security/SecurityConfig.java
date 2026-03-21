package com.iam.server.security;

import com.iam.server.config.IamProperties;
import com.iam.server.rbac.service.RbacQueryService;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * 安全配置主类
 * 集中配置了应用程序的安全策略，包括OAuth2授权服务器、资源服务器、密码编码器、令牌设置等。
 * 通过一系列@Bean方法提供Spring Security和Spring Authorization Server所需的各类安全组件。
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    /**
   * 创建一个BCrypt密码编码器的Bean。
   * 用于对用户密码进行安全的哈希处理和验证。
   *
   * @return 配置好的PasswordEncoder实例
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

    /**
   * 创建并配置OAuth2授权服务器的核心设置。
   * 设置了授权服务器的签发者(issuer)URL，该值从IamProperties中读取。
   *
   * @param properties 应用程序配置属性
   * @return 配置好的AuthorizationServerSettings对象
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings(final IamProperties properties) {
    return AuthorizationServerSettings.builder()
        .issuer(properties.getSecurity().getIssuer())
        .build();
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository(final DataSource dataSource) {
    return new JdbcRegisteredClientRepository(new JdbcTemplate(dataSource));
  }

  @Bean
  public OAuth2AuthorizationService authorizationService(final DataSource dataSource,
                                                         final RegisteredClientRepository registeredClientRepository) {
    return new JdbcOAuth2AuthorizationService(new JdbcTemplate(dataSource), registeredClientRepository);
  }

  @Bean
  public OAuth2AuthorizationConsentService authorizationConsentService(final DataSource dataSource,
                                                                       final RegisteredClientRepository registeredClientRepository) {
    return new JdbcOAuth2AuthorizationConsentService(new JdbcTemplate(dataSource), registeredClientRepository);
  }

    /**
   * 生成一个RSA密钥对的Bean，用于JWT令牌的签名和验证。
   * 该密钥对由Jwks工具类生成。
   *
   * @return 包含公钥和私钥的KeyPair对象
   */
  @Bean
  public KeyPair keyPair() {
    return Jwks.generateRsaKeyPair();
  }

    /**
   * 创建一个JWK Source Bean，用于向外部提供公钥信息（例如通过/.well-known/jwks.json端点）。
   * 它将当前应用的RSA公钥封装为一个不可变的JWK Set。
   *
   * @param keyPair 用于生成JWK的密钥对
   * @return JWKSource实例，包含应用的公钥
   */
  @Bean
  public JWKSource<SecurityContext> jwkSource(final KeyPair keyPair) {
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    java.security.interfaces.RSAPrivateKey privateKey = (java.security.interfaces.RSAPrivateKey) keyPair.getPrivate();
    RSAKey rsaKey = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build();
    return new ImmutableJWKSet<>(new JWKSet(rsaKey));
  }

    /**
   * 创建一个JwtDecoder Bean，用于解码和验证JWT令牌。
   * 配置了默认的JWT校验器，并额外添加了基于Redis的JTI撤销校验器，以确保已撤销的令牌无法通过验证。
   *
   * @param keyPair 用于验证JWT签名的密钥对中的公钥
   * @param properties 应用程序配置属性，用于获取签发者(issuer)
   * @param revocationService 用于检查令牌是否被撤销的服务
   * @return 配置好的NimbusJwtDecoder实例
   */
  @Bean
  public JwtDecoder jwtDecoder(final KeyPair keyPair,
                               final IamProperties properties,
                               final TokenRevocationService revocationService) {
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
    decoder.setJwtValidator(new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
        JwtValidators.createDefaultWithIssuer(properties.getSecurity().getIssuer()),
        new RedisJtiJwtValidator(revocationService)
    ));
    return decoder;
  }

    /**
   * 创建一个自定义的JWT令牌生成器。
   * 在生成访问令牌时，会向其中添加自定义声明，如用户的权限(authorities)和用户名(username)，以及一个唯一的令牌ID(jti)。
   *
   * @param rbacQueryService 用于查询用户权限的服务
   * @return 自定义的OAuth2TokenCustomizer实例
   */
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(final RbacQueryService rbacQueryService) {
    return context -> {
      if (!"access_token".equals(context.getTokenType().getValue())) {
        return;
      }
      Authentication principal = context.getPrincipal();
      String username = principal.getName();
      context.getClaims().id(UUID.randomUUID().toString());
      context.getClaims().claim("authorities", rbacQueryService.getAuthorityCodesByUsername(username));
      context.getClaims().claim("username", username);
    };
  }

    /**
   * 创建一个自定义的JwtAuthenticationConverter Bean。
   * 该转换器负责将JWT中的权限声明（默认为"authorities"）提取出来，并转换为Spring Security的GrantedAuthority对象。
   * 此配置移除了权限前缀，使权限字符串保持原样。
   *
   * @return 配置好的JwtAuthenticationConverter实例
   */
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
    converter.setAuthoritiesClaimName("authorities");
    converter.setAuthorityPrefix("");
    JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
    return authenticationConverter;
  }

    /**
   * 创建并返回令牌的默认设置。
   * 配置了访问令牌和刷新令牌的有效期，以及是否允许重用刷新令牌。
   *
   * @return 配置好的TokenSettings实例
   */
  @Bean
  public TokenSettings defaultTokenSettings() {
    return TokenSettings.builder()
        .accessTokenTimeToLive(Duration.ofMinutes(30))
        .refreshTokenTimeToLive(Duration.ofDays(7))
        .reuseRefreshTokens(false)
        .build();
  }

    /**
   * 创建并返回客户端的默认设置。
   * 配置了是否需要授权同意和是否需要证明密钥(PKCE)等选项。
   *
   * @return 配置好的ClientSettings实例
   */
  @Bean
  public ClientSettings defaultClientSettings() {
    return ClientSettings.builder()
        .requireAuthorizationConsent(false)
        .requireProofKey(true)
        .build();
  }

    /**
   * 配置OAuth2授权服务器的安全过滤器链。
   * 这是优先级最高的安全链（@Order(1)），处理所有与授权服务器相关的端点，如/oauth2/token等。
   */
  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(final HttpSecurity http,
                                                                    final CorsConfigurationSource cors) throws Exception {
    // 应用Spring Authorization Server的默认安全配置
    // 注意：此方法已过时，但为保持与当前版本的兼容性而保留
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    // 启用OpenID Connect 1.0支持
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .oidc(Customizer.withDefaults());
    // 配置跨域资源共享(CORS)，使用全局的CorsConfigurationSource
    http.cors(c -> c.configurationSource(cors));
    // 为特定的OAuth2端点禁用CSRF保护，因为这些端点通常由机器对机器调用
    http.csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/token", "/oauth2/introspect", "/oauth2/revoke"));
    http.exceptionHandling(ex -> ex
        .defaultAuthenticationEntryPointFor(
            new org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint("/login"),
            new org.springframework.security.web.util.matcher.MediaTypeRequestMatcher(org.springframework.http.MediaType.TEXT_HTML)
        ));
    return http.build();
  }

    /**
   * 配置应用程序主安全过滤器链。
   * 这是优先级次高的安全链（@Order(2)），处理API端点和Web页面的安全访问。
   */
  @Bean
  @Order(2)
  public SecurityFilterChain appSecurityFilterChain(final HttpSecurity http,
                                                    final CorsConfigurationSource cors,
                                                    final JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
    http.cors(c -> c.configurationSource(cors));
    http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
    // 配置HTTP请求的授权规则
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/h2-console/**").permitAll() // 允许访问H2控制台
        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/doc.html", "/webjars/**").permitAll() // 允许访问Swagger文档
        .requestMatchers("/error").permitAll() // 允许访问错误页面
        .requestMatchers("/login", "/login.css").permitAll() // 允许访问登录页面和CSS
        .requestMatchers("/api/**").authenticated() // API端点需要认证
        .anyRequest().permitAll()); // 其余请求允许访问
    http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
    http.formLogin(form -> form.loginPage("/login").permitAll());
    return http.build();
  }

    /**
   * 创建并配置一个DaoAuthenticationProvider的Bean。
   * 该提供者用于处理基于用户名和密码的身份验证，它使用自定义的UserDetailsService和PasswordEncoder。
   *
   * @param userAuthService 提供用户信息加载的服务实现
   * @param passwordEncoder 用于密码加密和比对的编码器
   * @return 配置好的DaoAuthenticationProvider实例
   */
  @Bean
  public org.springframework.security.authentication.dao.DaoAuthenticationProvider authenticationProvider(
      final com.iam.server.rbac.service.UserAuthService userAuthService,
      final PasswordEncoder passwordEncoder) {
    org.springframework.security.authentication.dao.DaoAuthenticationProvider provider =
        new org.springframework.security.authentication.dao.DaoAuthenticationProvider(userAuthService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }
}

