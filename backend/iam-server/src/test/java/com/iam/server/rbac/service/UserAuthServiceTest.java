package com.iam.server.rbac.service;

import com.iam.common.api.ApiException;
import com.iam.server.rbac.entity.DataScopeType;
import com.iam.server.rbac.entity.Permission;
import com.iam.server.rbac.entity.PermissionStatus;
import com.iam.server.rbac.entity.PermissionType;
import com.iam.server.rbac.entity.Role;
import com.iam.server.rbac.entity.RoleStatus;
import com.iam.server.rbac.entity.User;
import com.iam.server.rbac.entity.UserStatus;
import com.iam.server.rbac.repo.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserAuthServiceTest {
  @Test
  void loadUserByUsername_shouldBuildAuthorities() {
    UserRepository repo = Mockito.mock(UserRepository.class);

    Permission p = new Permission();
    p.setId("p1");
    p.setCode("sys:user:read");
    p.setName("n");
    p.setType(PermissionType.API);
    p.setSortNum(0);
    p.setStatus(PermissionStatus.ENABLED);

    Role r = new Role();
    r.setId("r1");
    r.setCode("ADMIN");
    r.setName("管理员");
    r.setLevelNum(1);
    r.setDataScope(DataScopeType.ALL);
    r.setStatus(RoleStatus.ENABLED);
    r.setPermissions(Set.of(p));

    User u = new User();
    u.setId("u1");
    u.setUsername("admin");
    u.setFullName("管理员");
    u.setStatus(UserStatus.ENABLED);
    u.setPasswordHash("hash");
    u.setRoles(Set.of(r));

    Mockito.when(repo.findWithRolesByUsername("admin")).thenReturn(Optional.of(u));

    UserAuthService svc = new UserAuthService(repo);
    UserDetails details = svc.loadUserByUsername("admin");
    Assertions.assertTrue(details.getAuthorities().stream().anyMatch(a -> "sys:user:read".equals(a.getAuthority())));
  }

  @Test
  void loadUserByUsername_shouldThrowWhenMissing() {
    UserRepository repo = Mockito.mock(UserRepository.class);
    Mockito.when(repo.findWithRolesByUsername("x")).thenReturn(Optional.empty());
    UserAuthService svc = new UserAuthService(repo);
    Assertions.assertThrows(UsernameNotFoundException.class, () -> svc.loadUserByUsername("x"));
  }

  @Test
  void loadUserByUsername_shouldThrowWhenDisabled() {
    UserRepository repo = Mockito.mock(UserRepository.class);
    User u = new User();
    u.setId("u1");
    u.setUsername("admin");
    u.setFullName("管理员");
    u.setStatus(UserStatus.DISABLED);
    u.setPasswordHash("hash");
    u.setRoles(Set.of());
    Mockito.when(repo.findWithRolesByUsername("admin")).thenReturn(Optional.of(u));
    UserAuthService svc = new UserAuthService(repo);
    Assertions.assertThrows(ApiException.class, () -> svc.loadUserByUsername("admin"));
  }
}

