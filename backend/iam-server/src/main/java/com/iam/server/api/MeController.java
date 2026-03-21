package com.iam.server.api;

import com.iam.server.rbac.service.RbacQueryService;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {
  private final RbacQueryService rbacQueryService;

  public MeController(final RbacQueryService rbacQueryService) {
    this.rbacQueryService = rbacQueryService;
  }

  @GetMapping
  public Map<String, Object> me(final Authentication authentication) {
    String username = authentication.getName();
    Set<String> authorities = rbacQueryService.getAuthorityCodesByUsername(username);
    return Map.of(
        "username", username,
        "authorities", authorities
    );
  }
}

