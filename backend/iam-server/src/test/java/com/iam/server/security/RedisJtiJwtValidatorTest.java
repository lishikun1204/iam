package com.iam.server.security;

import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

class RedisJtiJwtValidatorTest {
  @Test
  void validate_shouldFailWhenRevoked() {
    TokenRevocationService svc = Mockito.mock(TokenRevocationService.class);
    Mockito.when(svc.isRevoked("j1")).thenReturn(true);
    RedisJtiJwtValidator validator = new RedisJtiJwtValidator(svc);

    Jwt jwt = Jwt.withTokenValue("t")
        .header("alg", "none")
        .claim("jti", "j1")
        .subject("u")
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(60))
        .build();

    OAuth2TokenValidatorResult result = validator.validate(jwt);
    Assertions.assertTrue(result.hasErrors());
  }
}
