package com.iam.server.rbac.service;

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

class RbacQueryServiceImplTest {
  @Test
  void getAuthorityCodesByUsername_shouldReturnEnabledPermissionCodes() {
    UserRepository repo = Mockito.mock(UserRepository.class);

    Permission p1 = new Permission();
    p1.setId("p1");
    p1.setCode("sys:user:read");
    p1.setName("n");
    p1.setType(PermissionType.API);
    p1.setSortNum(0);
    p1.setStatus(PermissionStatus.ENABLED);

    Permission p2 = new Permission();
    p2.setId("p2");
    p2.setCode("sys:user:write");
    p2.setName("n");
    p2.setType(PermissionType.API);
    p2.setSortNum(0);
    p2.setStatus(PermissionStatus.DISABLED);

    Role role = new Role();
    role.setId("r1");
    role.setCode("R");
    role.setName("r");
    role.setLevelNum(1);
    role.setStatus(RoleStatus.ENABLED);
    role.setPermissions(Set.of(p1, p2));

    User user = new User();
    user.setId("u1");
    user.setUsername("admin");
    user.setFullName("管理员");
    user.setStatus(UserStatus.ENABLED);
    user.setPasswordHash("hash");
    user.setRoles(Set.of(role));

    Mockito.when(repo.findWithRolesByUsername("admin")).thenReturn(Optional.of(user));

    RbacQueryServiceImpl svc = new RbacQueryServiceImpl(repo);
    Set<String> codes = svc.getAuthorityCodesByUsername("admin");
    Assertions.assertTrue(codes.contains("sys:user:read"));
    Assertions.assertFalse(codes.contains("sys:user:write"));
  }
}

