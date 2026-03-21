package com.iam.server.rbac.service;

import com.iam.common.api.ApiException;
import com.iam.server.api.dto.CreateRoleRequest;
import com.iam.server.api.dto.SetRolePermissionsRequest;
import com.iam.server.api.dto.UpdateRoleRequest;
import com.iam.server.rbac.entity.DataScopeType;
import com.iam.server.rbac.entity.Permission;
import com.iam.server.rbac.entity.Role;
import com.iam.server.rbac.entity.RoleStatus;
import com.iam.server.rbac.repo.PermissionRepository;
import com.iam.server.rbac.repo.RoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RoleServiceTest {
  @Test
  void create_shouldThrowOnDuplicateCode() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Mockito.when(roleRepository.existsByCode("R")).thenReturn(true);

    RoleService service = new RoleService(roleRepository, permissionRepository);
    CreateRoleRequest req = new CreateRoleRequest();
    req.setCode("R");
    req.setName("角色");
    req.setLevelNum(1);
    req.setDataScope(DataScopeType.ALL);
    req.setStatus(RoleStatus.ENABLED);

    Assertions.assertThrows(ApiException.class, () -> service.create(req));
  }

  @Test
  void list_shouldReturnDtos() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Role role = new Role();
    role.setId("r1");
    role.setCode("R");
    role.setName("角色");
    role.setLevelNum(1);
    role.setDataScope(DataScopeType.ALL);
    role.setStatus(RoleStatus.ENABLED);
    Mockito.when(roleRepository.findAll()).thenReturn(List.of(role));

    RoleService service = new RoleService(roleRepository, permissionRepository);
    Assertions.assertEquals(1, service.list().size());
  }

  @Test
  void get_shouldReturnPermissionIds() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Permission p = new Permission();
    p.setId("p1");
    p.setCode("sys:user:read");
    Role role = new Role();
    role.setId("r1");
    role.setCode("R");
    role.setName("角色");
    role.setLevelNum(1);
    role.setDataScope(DataScopeType.ALL);
    role.setStatus(RoleStatus.ENABLED);
    role.setPermissions(Set.of(p));
    Mockito.when(roleRepository.findWithPermissionsById("r1")).thenReturn(Optional.of(role));

    RoleService service = new RoleService(roleRepository, permissionRepository);
    Assertions.assertTrue(service.get("r1").getPermissionIds().contains("p1"));
  }

  @Test
  void delete_shouldForbidBuiltinAdmin() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Role role = new Role();
    role.setId("r1");
    role.setCode("ADMIN");
    role.setName("管理员");
    role.setLevelNum(1);
    role.setDataScope(DataScopeType.ALL);
    role.setStatus(RoleStatus.ENABLED);
    Mockito.when(roleRepository.findById("r1")).thenReturn(Optional.of(role));

    RoleService service = new RoleService(roleRepository, permissionRepository);
    Assertions.assertThrows(ApiException.class, () -> service.delete("r1"));
  }

  @Test
  void create_shouldAttachPermissions() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Mockito.when(roleRepository.existsByCode("R")).thenReturn(false);
    Permission p = new Permission();
    p.setId("p1");
    p.setCode("sys:user:read");
    Mockito.when(permissionRepository.findAllById(Set.of("p1"))).thenReturn(List.of(p));
    Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

    RoleService service = new RoleService(roleRepository, permissionRepository);
    CreateRoleRequest req = new CreateRoleRequest();
    req.setCode("R");
    req.setName("角色");
    req.setLevelNum(1);
    req.setDataScope(DataScopeType.ALL);
    req.setStatus(RoleStatus.ENABLED);
    req.setPermissionIds(Set.of("p1"));

    service.create(req);
    Mockito.verify(roleRepository).save(Mockito.argThat(r -> r.getPermissions().size() == 1));
  }

  @Test
  void update_shouldSave() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Role role = new Role();
    role.setId("r1");
    role.setCode("R");
    role.setName("old");
    role.setLevelNum(1);
    role.setDataScope(DataScopeType.ALL);
    role.setStatus(RoleStatus.ENABLED);
    Mockito.when(roleRepository.findById("r1")).thenReturn(Optional.of(role));
    Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

    RoleService service = new RoleService(roleRepository, permissionRepository);
    UpdateRoleRequest req = new UpdateRoleRequest();
    req.setName("new");
    req.setLevelNum(2);
    req.setDataScope(DataScopeType.ALL);
    req.setStatus(RoleStatus.ENABLED);
    Assertions.assertEquals("new", service.update("r1", req).getName());
  }

  @Test
  void delete_shouldDeleteWhenNotBuiltin() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Role role = new Role();
    role.setId("r2");
    role.setCode("R");
    role.setName("角色");
    role.setLevelNum(1);
    role.setDataScope(DataScopeType.ALL);
    role.setStatus(RoleStatus.ENABLED);
    Mockito.when(roleRepository.findById("r2")).thenReturn(Optional.of(role));

    RoleService service = new RoleService(roleRepository, permissionRepository);
    service.delete("r2");
    Mockito.verify(roleRepository).delete(Mockito.any(Role.class));
  }

  @Test
  void setPermissions_shouldThrowIfMissing() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Role role = new Role();
    role.setId("r1");
    role.setCode("R");
    role.setName("角色");
    role.setLevelNum(1);
    role.setDataScope(DataScopeType.ALL);
    role.setStatus(RoleStatus.ENABLED);
    Mockito.when(roleRepository.findWithPermissionsById("r1")).thenReturn(Optional.of(role));
    Mockito.when(permissionRepository.findAllById(Set.of("p1"))).thenReturn(List.of());

    RoleService service = new RoleService(roleRepository, permissionRepository);
    SetRolePermissionsRequest req = new SetRolePermissionsRequest();
    req.setPermissionIds(Set.of("p1"));

    Assertions.assertThrows(ApiException.class, () -> service.setPermissions("r1", req));
  }

  @Test
  void setPermissions_shouldSaveWhenAllExist() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Role role = new Role();
    role.setId("r1");
    role.setCode("R");
    role.setName("角色");
    role.setLevelNum(1);
    role.setDataScope(DataScopeType.ALL);
    role.setStatus(RoleStatus.ENABLED);
    Mockito.when(roleRepository.findWithPermissionsById("r1")).thenReturn(Optional.of(role));
    Permission p = new Permission();
    p.setId("p1");
    p.setCode("sys:user:read");
    Mockito.when(permissionRepository.findAllById(Set.of("p1"))).thenReturn(List.of(p));
    Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

    RoleService service = new RoleService(roleRepository, permissionRepository);
    SetRolePermissionsRequest req = new SetRolePermissionsRequest();
    req.setPermissionIds(Set.of("p1"));
    service.setPermissions("r1", req);
    Mockito.verify(roleRepository).save(Mockito.argThat(r -> r.getPermissions().size() == 1));
  }

  @Test
  void update_shouldThrowWhenNotFound() {
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PermissionRepository permissionRepository = Mockito.mock(PermissionRepository.class);
    Mockito.when(roleRepository.findById("r1")).thenReturn(Optional.empty());

    RoleService service = new RoleService(roleRepository, permissionRepository);
    UpdateRoleRequest req = new UpdateRoleRequest();
    req.setName("n");
    req.setLevelNum(1);
    req.setDataScope(DataScopeType.ALL);
    req.setStatus(RoleStatus.ENABLED);

    Assertions.assertThrows(ApiException.class, () -> service.update("r1", req));
  }
}
