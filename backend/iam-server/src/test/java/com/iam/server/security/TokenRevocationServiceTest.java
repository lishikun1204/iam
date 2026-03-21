package com.iam.server.security;

import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class TokenRevocationServiceTest {
  @Test
  void revokeJti_shouldWriteKeyWithTtl() {
    StringRedisTemplate redis = Mockito.mock(StringRedisTemplate.class);
    ValueOperations<String, String> ops = Mockito.mock(ValueOperations.class);
    Mockito.when(redis.opsForValue()).thenReturn(ops);

    ObjectProvider<StringRedisTemplate> provider = Mockito.mock(ObjectProvider.class);
    Mockito.when(provider.getIfAvailable()).thenReturn(redis);

    TokenRevocationService service = new TokenRevocationService(provider, true);
    service.revokeJti("jti-1", Instant.now().plusSeconds(60));

    Mockito.verify(ops).set(Mockito.eq("iam:token:revoked:jti-1"), Mockito.eq("1"), Mockito.any());
  }

  @Test
  void isRevoked_shouldReturnFalseOnBlank() {
    ObjectProvider<StringRedisTemplate> provider = Mockito.mock(ObjectProvider.class);
    TokenRevocationService service = new TokenRevocationService(provider, true);
    Assertions.assertFalse(service.isRevoked(""));
  }

  @Test
  void isRevoked_shouldReturnTrueWhenKeyExists() {
    StringRedisTemplate redis = Mockito.mock(StringRedisTemplate.class);
    ValueOperations<String, String> ops = Mockito.mock(ValueOperations.class);
    Mockito.when(redis.opsForValue()).thenReturn(ops);
    Mockito.when(ops.get("iam:token:revoked:jti-1")).thenReturn("1");

    ObjectProvider<StringRedisTemplate> provider = Mockito.mock(ObjectProvider.class);
    Mockito.when(provider.getIfAvailable()).thenReturn(redis);

    TokenRevocationService service = new TokenRevocationService(provider, true);
    Assertions.assertTrue(service.isRevoked("jti-1"));
  }

  @Test
  void revokeJti_shouldUseFallbackTtlWhenExpired() {
    StringRedisTemplate redis = Mockito.mock(StringRedisTemplate.class);
    ValueOperations<String, String> ops = Mockito.mock(ValueOperations.class);
    Mockito.when(redis.opsForValue()).thenReturn(ops);

    ObjectProvider<StringRedisTemplate> provider = Mockito.mock(ObjectProvider.class);
    Mockito.when(provider.getIfAvailable()).thenReturn(redis);

    TokenRevocationService service = new TokenRevocationService(provider, true);
    service.revokeJti("jti-2", Instant.now().minusSeconds(5));
    Mockito.verify(ops).set(Mockito.eq("iam:token:revoked:jti-2"), Mockito.eq("1"), Mockito.any());
  }

  @Test
  void noRedisMode_shouldUseInMemoryStore() {
    ObjectProvider<StringRedisTemplate> provider = Mockito.mock(ObjectProvider.class);
    Mockito.when(provider.getIfAvailable()).thenReturn(null);

    TokenRevocationService service = new TokenRevocationService(provider, false);
    service.revokeJti("jti-mem", Instant.now().plusSeconds(60));
    Assertions.assertTrue(service.isRevoked("jti-mem"));
  }
}
