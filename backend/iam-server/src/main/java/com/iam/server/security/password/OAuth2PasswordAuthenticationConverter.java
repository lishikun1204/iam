package com.iam.server.security.password;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class OAuth2PasswordAuthenticationConverter implements AuthenticationConverter {
  @Override
  public Authentication convert(final HttpServletRequest request) {
    String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
    if (!OAuth2PasswordAuthenticationToken.PASSWORD_GRANT_TYPE.getValue().equals(grantType)) {
      return null;
    }

    Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

    String username = request.getParameter(OAuth2ParameterNames.USERNAME);
    String password = request.getParameter(OAuth2ParameterNames.PASSWORD);
    String scope = request.getParameter(OAuth2ParameterNames.SCOPE);

    Set<String> scopes = new HashSet<>();
    if (scope != null && !scope.isBlank()) {
      scopes.addAll(Arrays.asList(scope.split("\\s+")));
    }

    Map<String, Object> additionalParameters = new HashMap<>();
    request.getParameterMap().forEach((k, v) -> {
      if (v != null && v.length > 0) {
        additionalParameters.put(k, v[0]);
      }
    });

    return new OAuth2PasswordAuthenticationToken(clientPrincipal, username, password, scopes, additionalParameters);
  }
}
