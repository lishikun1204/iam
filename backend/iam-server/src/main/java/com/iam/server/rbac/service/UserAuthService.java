package com.iam.server.rbac.service;

import com.iam.common.api.ApiException;
import com.iam.common.api.ApiErrorCode;
import com.iam.server.rbac.entity.Permission;
import com.iam.server.rbac.entity.PermissionStatus;
import com.iam.server.rbac.entity.Role;
import com.iam.server.rbac.entity.RoleStatus;
import com.iam.server.rbac.entity.User;
import com.iam.server.rbac.entity.UserStatus;
import com.iam.server.rbac.repo.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService {
  private final UserRepository userRepository;

  public UserAuthService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    User user = userRepository.findWithRolesByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    if (user.getStatus() != UserStatus.ENABLED) {
      throw new ApiException(ApiErrorCode.FORBIDDEN, "用户已被禁用");
    }
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    for (Role role : user.getRoles()) {
      if (role.getStatus() != RoleStatus.ENABLED) {
        continue;
      }
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
      for (Permission permission : role.getPermissions()) {
        if (permission.getStatus() == PermissionStatus.ENABLED) {
          authorities.add(new SimpleGrantedAuthority(permission.getCode()));
        }
      }
    }
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPasswordHash())
        .authorities(authorities)
        .build();
  }
}

