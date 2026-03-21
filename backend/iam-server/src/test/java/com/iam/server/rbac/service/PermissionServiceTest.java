package com.iam.server.rbac.service;

import com.iam.common.api.ApiException;
import com.iam.server.api.dto.CreatePermissionRequest;
import com.iam.server.api.dto.UpdatePermissionRequest;
import com.iam.server.rbac.entity.Permission;
import com.iam.server.rbac.entity.PermissionStatus;
import com.iam.server.rbac.entity.PermissionType;
import com.iam.server.rbac.repo.PermissionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PermissionServiceTest {
  @Test
  void list_shouldReturnDtos() {
    PermissionRepository repo = Mockito.mock(PermissionRepository.class);
    Permission p = new Permission();
    p.setId("p1");
    p.setCode("c");
    p.setName("n");
    p.setType(PermissionType.API);
    p.setSortNum(0);
    p.setStatus(PermissionStatus.ENABLED);
    Mockito.when(repo.findAll()).thenReturn(List.of(p));
    PermissionService service = new PermissionService(repo);
    Assertions.assertEquals(1, service.list().size());
  }

  @Test
  void create_shouldThrowOnDuplicateCode() {
    PermissionRepository repo = Mockito.mock(PermissionRepository.class);
    Mockito.when(repo.existsByCode("c")).thenReturn(true);
    PermissionService service = new PermissionService(repo);

    CreatePermissionRequest req = new CreatePermissionRequest();
    req.setCode("c");
    req.setName("n");
    req.setType(PermissionType.API);
    req.setSortNum(0);
    req.setStatus(PermissionStatus.ENABLED);

    Assertions.assertThrows(ApiException.class, () -> service.create(req));
  }

  @Test
  void create_shouldSaveAndReturnDto() {
    PermissionRepository repo = Mockito.mock(PermissionRepository.class);
    Mockito.when(repo.existsByCode("c")).thenReturn(false);
    Mockito.when(repo.save(Mockito.any(Permission.class))).thenAnswer(inv -> {
      Permission p = inv.getArgument(0);
      p.setId("p1");
      return p;
    });
    PermissionService service = new PermissionService(repo);

    CreatePermissionRequest req = new CreatePermissionRequest();
    req.setCode("c");
    req.setName("n");
    req.setType(PermissionType.API);
    req.setSortNum(0);
    req.setStatus(PermissionStatus.ENABLED);
    Assertions.assertEquals("c", service.create(req).getCode());
  }

  @Test
  void update_shouldThrowWhenNotFound() {
    PermissionRepository repo = Mockito.mock(PermissionRepository.class);
    Mockito.when(repo.findById("p1")).thenReturn(Optional.empty());
    PermissionService service = new PermissionService(repo);

    UpdatePermissionRequest req = new UpdatePermissionRequest();
    req.setName("n");
    req.setType(PermissionType.API);
    req.setSortNum(0);
    req.setStatus(PermissionStatus.ENABLED);

    Assertions.assertThrows(ApiException.class, () -> service.update("p1", req));
  }

  @Test
  void update_shouldSaveAndReturnDto() {
    PermissionRepository repo = Mockito.mock(PermissionRepository.class);
    Permission p = new Permission();
    p.setId("p1");
    p.setCode("c");
    p.setName("old");
    p.setType(PermissionType.API);
    p.setSortNum(0);
    p.setStatus(PermissionStatus.ENABLED);
    Mockito.when(repo.findById("p1")).thenReturn(Optional.of(p));
    Mockito.when(repo.save(Mockito.any(Permission.class))).thenAnswer(inv -> inv.getArgument(0));
    PermissionService service = new PermissionService(repo);

    UpdatePermissionRequest req = new UpdatePermissionRequest();
    req.setName("new");
    req.setType(PermissionType.API);
    req.setSortNum(0);
    req.setStatus(PermissionStatus.ENABLED);
    Assertions.assertEquals("new", service.update("p1", req).getName());
  }

  @Test
  void get_shouldReturnDto() {
    PermissionRepository repo = Mockito.mock(PermissionRepository.class);
    Permission p = new Permission();
    p.setId("p1");
    p.setCode("sys:user:read");
    p.setName("n");
    p.setType(PermissionType.API);
    p.setSortNum(0);
    p.setStatus(PermissionStatus.ENABLED);
    Mockito.when(repo.findById("p1")).thenReturn(Optional.of(p));
    PermissionService service = new PermissionService(repo);

    Assertions.assertEquals("p1", service.get("p1").getId());
  }

  @Test
  void get_shouldThrowWhenNotFound() {
    PermissionRepository repo = Mockito.mock(PermissionRepository.class);
    Mockito.when(repo.findById("x")).thenReturn(Optional.empty());
    PermissionService service = new PermissionService(repo);
    Assertions.assertThrows(ApiException.class, () -> service.get("x"));
  }
}
