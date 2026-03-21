package com.iam.server.rbac.service;

import com.iam.common.api.ApiException;
import com.iam.server.api.dto.CreateDepartmentRequest;
import com.iam.server.api.dto.UpdateDepartmentRequest;
import com.iam.server.rbac.entity.Department;
import com.iam.server.rbac.repo.DepartmentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DepartmentServiceTest {
  @Test
  void list_shouldReturnDtos() {
    DepartmentRepository repo = Mockito.mock(DepartmentRepository.class);
    Department d = new Department();
    d.setId("d1");
    d.setCode("D");
    d.setName("部门");
    d.setSortNum(0);
    Mockito.when(repo.findAll()).thenReturn(List.of(d));
    DepartmentService service = new DepartmentService(repo);
    Assertions.assertEquals(1, service.list().size());
  }

  @Test
  void get_shouldReturnDto() {
    DepartmentRepository repo = Mockito.mock(DepartmentRepository.class);
    Department d = new Department();
    d.setId("d1");
    d.setCode("D");
    d.setName("部门");
    d.setSortNum(0);
    Mockito.when(repo.findById("d1")).thenReturn(Optional.of(d));
    DepartmentService service = new DepartmentService(repo);
    Assertions.assertEquals("D", service.get("d1").getCode());
  }

  @Test
  void create_shouldSaveAndReturnDto() {
    DepartmentRepository repo = Mockito.mock(DepartmentRepository.class);
    Mockito.when(repo.existsByCode("D")).thenReturn(false);
    Mockito.when(repo.save(Mockito.any(Department.class))).thenAnswer(inv -> {
      Department d = inv.getArgument(0);
      d.setId("d1");
      return d;
    });
    DepartmentService service = new DepartmentService(repo);

    CreateDepartmentRequest req = new CreateDepartmentRequest();
    req.setCode("D");
    req.setName("部门");
    req.setSortNum(0);

    Assertions.assertEquals("D", service.create(req).getCode());
  }

  @Test
  void create_shouldThrowOnDuplicateCode() {
    DepartmentRepository repo = Mockito.mock(DepartmentRepository.class);
    Mockito.when(repo.existsByCode("D")).thenReturn(true);
    DepartmentService service = new DepartmentService(repo);

    CreateDepartmentRequest req = new CreateDepartmentRequest();
    req.setCode("D");
    req.setName("部门");
    req.setSortNum(0);

    Assertions.assertThrows(ApiException.class, () -> service.create(req));
  }

  @Test
  void update_shouldThrowWhenNotFound() {
    DepartmentRepository repo = Mockito.mock(DepartmentRepository.class);
    Mockito.when(repo.findById("d1")).thenReturn(Optional.empty());
    DepartmentService service = new DepartmentService(repo);

    UpdateDepartmentRequest req = new UpdateDepartmentRequest();
    req.setName("n");
    req.setSortNum(0);

    Assertions.assertThrows(ApiException.class, () -> service.update("d1", req));
  }

  @Test
  void update_shouldSaveAndReturnDto() {
    DepartmentRepository repo = Mockito.mock(DepartmentRepository.class);
    Department d = new Department();
    d.setId("d1");
    d.setCode("D");
    d.setName("old");
    d.setSortNum(0);
    Mockito.when(repo.findById("d1")).thenReturn(Optional.of(d));
    Mockito.when(repo.save(Mockito.any(Department.class))).thenAnswer(inv -> inv.getArgument(0));
    DepartmentService service = new DepartmentService(repo);

    UpdateDepartmentRequest req = new UpdateDepartmentRequest();
    req.setName("new");
    req.setSortNum(1);

    Assertions.assertEquals("new", service.update("d1", req).getName());
  }

  @Test
  void delete_shouldForbidRoot() {
    DepartmentRepository repo = Mockito.mock(DepartmentRepository.class);
    Department d = new Department();
    d.setId("d1");
    d.setCode("ROOT");
    d.setName("总部");
    d.setSortNum(0);
    Mockito.when(repo.findById("d1")).thenReturn(Optional.of(d));
    DepartmentService service = new DepartmentService(repo);

    Assertions.assertThrows(ApiException.class, () -> service.delete("d1"));
  }

  @Test
  void delete_shouldDeleteWhenNotRoot() {
    DepartmentRepository repo = Mockito.mock(DepartmentRepository.class);
    Department d = new Department();
    d.setId("d2");
    d.setCode("D");
    d.setName("部门");
    d.setSortNum(0);
    Mockito.when(repo.findById("d2")).thenReturn(Optional.of(d));
    DepartmentService service = new DepartmentService(repo);

    service.delete("d2");
    Mockito.verify(repo).delete(Mockito.any(Department.class));
  }
}
