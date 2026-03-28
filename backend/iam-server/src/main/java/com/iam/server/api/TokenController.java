package com.iam.server.api;

import com.iam.common.api.ApiErrorCode;
import com.iam.common.api.ApiException;
import com.iam.server.security.TokenRevocationService;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {
  private final JwtDecoder jwtDecoder;
  private final TokenRevocationService tokenRevocationService;
  private final OAuth2AuthorizationService authorizationService;

  public TokenController(
      final JwtDecoder jwtDecoder,
      final TokenRevocationService tokenRevocationService,
      final OAuth2AuthorizationService authorizationService) {
    this.jwtDecoder = jwtDecoder;
    this.tokenRevocationService = tokenRevocationService;
    this.authorizationService = authorizationService;
  }

  @PostMapping("/revoke")
  @PreAuthorize("hasAuthority('sys:menu:console')")
  public Map<String, Object> revoke(@RequestBody final RevokeTokenRequest req) {
    Jwt jwt;
    try {
      jwt = jwtDecoder.decode(req.getAccessToken());
    } catch (Exception ex) {
      throw new ApiException(ApiErrorCode.VALIDATION_ERROR, "access_token 无效");
    }
    Instant exp = jwt.getExpiresAt();
    if (exp == null) {
      throw new ApiException(ApiErrorCode.VALIDATION_ERROR, "access_token 缺少 exp");
    }
    tokenRevocationService.revokeJti(jwt.getId(), exp);
    OAuth2Authorization authorization =
        authorizationService.findByToken(req.getAccessToken(), OAuth2TokenType.ACCESS_TOKEN);
    if (authorization != null) {
      authorizationService.remove(authorization);
    }
    return Map.of("revoked", true);
  }

  public static class RevokeTokenRequest {
    @NotBlank
    private String accessToken;

    public String getAccessToken() {
      return accessToken;
    }

    public void setAccessToken(final String accessToken) {
      this.accessToken = accessToken;
    }
  }
}

