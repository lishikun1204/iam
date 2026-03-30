package com.iam.server.security.password;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

public class OAuth2PasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {
  public static final AuthorizationGrantType PASSWORD_GRANT_TYPE = new AuthorizationGrantType("password");

  private final String username;
  private final String password;
  private final Set<String> scopes;

  public OAuth2PasswordAuthenticationToken(
      final Authentication clientPrincipal,
      final String username,
      final String password,
      final Set<String> scopes,
      final Map<String, Object> additionalParameters) {
    super(PASSWORD_GRANT_TYPE, clientPrincipal, additionalParameters != null ? additionalParameters : Collections.emptyMap());
    this.username = username;
    this.password = password;
    this.scopes = scopes != null ? Set.copyOf(scopes) : Set.of();
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public Set<String> getScopes() {
    return scopes;
  }
}

