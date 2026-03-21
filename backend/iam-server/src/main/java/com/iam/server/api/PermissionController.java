package com.iam.server.api;

import com.iam.server.api.dto.CreatePermissionRequest;
import com.iam.server.api.dto.PermissionDto;
import com.iam.server.api.dto.UpdatePermissionRequest;
import com.iam.server.rbac.service.PermissionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
  private final PermissionService permissionService;

  public PermissionController(final PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('sys:perm:read')")
  public List<PermissionDto> list() {
    return permissionService.list();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:perm:read')")
  public PermissionDto get(@PathVariable("id") final String id) {
    return permissionService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('sys:perm:write')")
  public PermissionDto create(@Valid @RequestBody final CreatePermissionRequest req) {
    return permissionService.create(req);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:perm:write')")
  public PermissionDto update(@PathVariable("id") final String id, @Valid @RequestBody final UpdatePermissionRequest req) {
    return permissionService.update(id, req);
  }
}

