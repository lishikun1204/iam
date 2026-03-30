package com.iam.server.security.password;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

public class PasswordPublicClientAuthenticationConverter implements AuthenticationConverter {
  @Override
  public OAuth2ClientAuthenticationToken convert(final HttpServletRequest request) {
    String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
    if (!OAuth2PasswordAuthenticationToken.PASSWORD_GRANT_TYPE.getValue().equals(grantType)) {
      return null;
    }

    String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
    if (!StringUtils.hasText(clientId)) {
      return null;
    }

    String clientSecret = request.getParameter(OAuth2ParameterNames.CLIENT_SECRET);
    if (StringUtils.hasText(clientSecret)) {
      return null;
    }

    Map<String, Object> additionalParameters = new HashMap<>();
    request.getParameterMap().forEach((k, v) -> {
      if (v != null && v.length > 0) {
        additionalParameters.put(k, v[0]);
      }
    });

    return new OAuth2ClientAuthenticationToken(
        clientId,
        ClientAuthenticationMethod.NONE,
        null,
        additionalParameters);
  }
}

