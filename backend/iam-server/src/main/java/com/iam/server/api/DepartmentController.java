package com.iam.server.api;

import com.iam.server.api.dto.CreateDepartmentRequest;
import com.iam.server.api.dto.DepartmentDto;
import com.iam.server.api.dto.UpdateDepartmentRequest;
import com.iam.server.rbac.service.DepartmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/depts")
public class DepartmentController {
  private final DepartmentService departmentService;

  public DepartmentController(final DepartmentService departmentService) {
    this.departmentService = departmentService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('sys:dept:read')")
  public List<DepartmentDto> list() {
    return departmentService.list();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:dept:read')")
  public DepartmentDto get(@PathVariable("id") final String id) {
    return departmentService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('sys:dept:write')")
  public DepartmentDto create(@Valid @RequestBody final CreateDepartmentRequest req) {
    return departmentService.create(req);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:dept:write')")
  public DepartmentDto update(@PathVariable("id") final String id, @Valid @RequestBody final UpdateDepartmentRequest req) {
    return departmentService.update(id, req);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:dept:write')")
  public void delete(@PathVariable("id") final String id) {
    departmentService.delete(id);
  }
}

