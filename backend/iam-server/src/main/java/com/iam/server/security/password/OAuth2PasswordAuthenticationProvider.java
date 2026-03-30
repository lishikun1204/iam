package com.iam.server.security.password;

import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.StringUtils;

public class OAuth2PasswordAuthenticationProvider implements AuthenticationProvider {
  private final OAuth2AuthorizationService authorizationService;
  private final OAuth2TokenGenerator<?> tokenGenerator;
  private final AuthenticationProvider userAuthenticationProvider;

  public OAuth2PasswordAuthenticationProvider(
      final OAuth2AuthorizationService authorizationService,
      final OAuth2TokenGenerator<?> tokenGenerator,
      final AuthenticationProvider userAuthenticationProvider) {
    this.authorizationService = authorizationService;
    this.tokenGenerator = tokenGenerator;
    this.userAuthenticationProvider = userAuthenticationProvider;
  }

  @Override
  public Authentication authenticate(final Authentication authentication) {
    OAuth2PasswordAuthenticationToken passwordGrant = (OAuth2PasswordAuthenticationToken) authentication;

    OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(passwordGrant);
    RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
    if (registeredClient == null) {
      throw invalidClient();
    }

    if (!registeredClient.getAuthorizationGrantTypes().contains(OAuth2PasswordAuthenticationToken.PASSWORD_GRANT_TYPE)) {
      throw unauthorizedClient();
    }

    String username = passwordGrant.getUsername();
    String password = passwordGrant.getPassword();
    if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
      throw invalidRequest("username/password required");
    }

    Authentication userAuthentication =
        userAuthenticationProvider.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(username, password));
    if (userAuthentication == null || !userAuthentication.isAuthenticated()) {
      throw invalidGrant();
    }

    Set<String> requestedScopes = passwordGrant.getScopes();
    Set<String> authorizedScopes;
    if (requestedScopes == null || requestedScopes.isEmpty()) {
      authorizedScopes = registeredClient.getScopes();
    } else {
      if (!registeredClient.getScopes().containsAll(requestedScopes)) {
        throw invalidScope();
      }
      authorizedScopes = requestedScopes;
    }

    DefaultOAuth2TokenContext.Builder tokenContextBuilder =
        DefaultOAuth2TokenContext.builder()
            .registeredClient(registeredClient)
            .principal(userAuthentication)
            .authorizationServerContext(AuthorizationServerContextHolder.getContext())
            .authorizedScopes(authorizedScopes)
            .authorizationGrantType(OAuth2PasswordAuthenticationToken.PASSWORD_GRANT_TYPE)
            .authorizationGrant(passwordGrant);

    OAuth2Authorization.Builder authorizationBuilder =
        OAuth2Authorization.withRegisteredClient(registeredClient)
            .principalName(userAuthentication.getName())
            .authorizationGrantType(OAuth2PasswordAuthenticationToken.PASSWORD_GRANT_TYPE)
            .authorizedScopes(authorizedScopes)
            .attribute(Principal.class.getName(), userAuthentication);

    OAuth2TokenContext accessTokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
    Object generatedAccessToken = tokenGenerator.generate(accessTokenContext);
    if (generatedAccessToken == null) {
      throw serverError();
    }

    OAuth2AccessToken accessToken;
    Map<String, Object> accessTokenMetadata = new HashMap<>();
    if (generatedAccessToken instanceof Jwt jwt) {
      Instant issuedAt = jwt.getIssuedAt();
      Instant expiresAt = jwt.getExpiresAt();
      accessToken =
          new OAuth2AccessToken(
              OAuth2AccessToken.TokenType.BEARER, jwt.getTokenValue(), issuedAt, expiresAt, authorizedScopes);
      accessTokenMetadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, jwt.getClaims());
    } else if (generatedAccessToken instanceof OAuth2AccessToken token) {
      accessToken = token;
    } else {
      throw serverError();
    }

    authorizationBuilder.token(accessToken, metadata -> metadata.putAll(accessTokenMetadata));

    OAuth2RefreshToken refreshToken = null;
    if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
      OAuth2TokenContext refreshTokenContext =
          tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
      Object generatedRefreshToken = tokenGenerator.generate(refreshTokenContext);
      if (generatedRefreshToken instanceof OAuth2RefreshToken token) {
        refreshToken = token;
        authorizationBuilder.refreshToken(refreshToken);
      }
    }

    OAuth2Authorization authorization = authorizationBuilder.build();
    authorizationService.save(authorization);

    Map<String, Object> additionalParameters = new HashMap<>();
    return new OAuth2AccessTokenAuthenticationToken(
        registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
  }

  @Override
  public boolean supports(final Class<?> authentication) {
    return OAuth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(final Authentication authentication) {
    Object clientPrincipal = authentication.getPrincipal();
    if (!(clientPrincipal instanceof OAuth2ClientAuthenticationToken oauth2Client) || !oauth2Client.isAuthenticated()) {
      throw invalidClient();
    }
    return oauth2Client;
  }

  private static OAuth2AuthenticationException invalidClient() {
    return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT));
  }

  private static OAuth2AuthenticationException unauthorizedClient() {
    return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT));
  }

  private static OAuth2AuthenticationException invalidGrant() {
    return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));
  }

  private static OAuth2AuthenticationException invalidScope() {
    return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE));
  }

  private static OAuth2AuthenticationException invalidRequest(final String description) {
    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, description, null);
    return new OAuth2AuthenticationException(error);
  }

  private static OAuth2AuthenticationException serverError() {
    return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR));
  }
}
