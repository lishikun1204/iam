package com.iam.server.rbac.service;

import com.iam.server.rbac.entity.Permission;
import com.iam.server.rbac.entity.PermissionStatus;
import com.iam.server.rbac.entity.Role;
import com.iam.server.rbac.entity.RoleStatus;
import com.iam.server.rbac.entity.User;
import com.iam.server.rbac.entity.UserStatus;
import com.iam.server.rbac.repo.UserRepository;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RbacQueryServiceImpl implements RbacQueryService {
  private final UserRepository userRepository;

  public RbacQueryServiceImpl(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Cacheable(cacheNames = "rbac:userAuthorities", key = "#p0")
  public Set<String> getAuthorityCodesByUsername(final String username) {
    User user = userRepository.findWithRolesByUsername(username).orElse(null);
    if (user == null || user.getStatus() != UserStatus.ENABLED) {
      return Set.of();
    }
    Set<String> codes = new LinkedHashSet<>();
    for (Role role : user.getRoles()) {
      if (role.getStatus() != RoleStatus.ENABLED) {
        continue;
      }
      for (Permission permission : role.getPermissions()) {
        if (permission.getStatus() == PermissionStatus.ENABLED) {
          codes.add(permission.getCode());
        }
      }
    }
    return codes;
  }
}
