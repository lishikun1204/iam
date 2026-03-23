package com.iam.server.security;

import java.util.List;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * 基于Redis的JWT ID (JTI) 验证器
 * 实现Spring Security的OAuth2TokenValidator接口，用于验证JWT令牌是否已被撤销。
 * 通过查询Redis中存储的已撤销令牌ID列表来判断令牌的有效性。
 */
public class RedisJtiJwtValidator implements OAuth2TokenValidator<Jwt> {
  private final TokenRevocationService revocationService;

  /**
   * 构造函数，用于注入令牌撤销服务。
   *
   * @param revocationService 提供查询令牌是否被撤销功能的服务
   */
  public RedisJtiJwtValidator(final TokenRevocationService revocationService) {
    this.revocationService = revocationService;
  }

  /**
   * 验证给定的JWT令牌是否有效（未被撤销）。
   * 此方法从令牌中提取JWT ID (JTI)，并检查其是否存在于已撤销列表中。
   *
   * @param token 待验证的JWT令牌
   * @return 如果令牌有效则返回成功结果；如果令牌已被撤销，则返回包含错误信息的失败结果
   */
  @Override
  public OAuth2TokenValidatorResult validate(final Jwt token) {
    String jti = token.getId();
    if (revocationService.isRevoked(jti)) {
      OAuth2Error error = new OAuth2Error("invalid_token", "token revoked", null);
      return OAuth2TokenValidatorResult.failure(List.of(error));
    }
    return OAuth2TokenValidatorResult.success();
  }
}
