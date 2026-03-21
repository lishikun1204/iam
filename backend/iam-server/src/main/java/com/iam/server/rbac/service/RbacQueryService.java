package com.iam.server.rbac.service;

import java.util.Set;

public interface RbacQueryService {
  Set<String> getAuthorityCodesByUsername(String username);
}

