package com.iam.server.api;

import com.iam.server.security.TokenRevocationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final TokenRevocationService tokenRevocationService;
  private final OAuth2AuthorizationService authorizationService;

  public AuthController(
      final TokenRevocationService tokenRevocationService,
      final OAuth2AuthorizationService authorizationService) {
    this.tokenRevocationService = tokenRevocationService;
    this.authorizationService = authorizationService;
  }

  @PostMapping("/logout")
  public Map<String, Object> logout(
      final Authentication authentication,
      final HttpServletRequest request,
      final HttpServletResponse response) {
    Jwt jwt = null;
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      jwt = jwtAuth.getToken();
    }

    if (jwt != null) {
      Instant exp = jwt.getExpiresAt();
      if (exp != null) {
        tokenRevocationService.revokeJti(jwt.getId(), exp);
      }
      OAuth2Authorization authorization =
          authorizationService.findByToken(jwt.getTokenValue(), OAuth2TokenType.ACCESS_TOKEN);
      if (authorization != null) {
        authorizationService.remove(authorization);
      }
    }

    new SecurityContextLogoutHandler().logout(request, response, authentication);
    return Map.of("loggedOut", true);
  }
}
