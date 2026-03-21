package com.iam.server.api;

import com.iam.server.api.dto.CreateRoleRequest;
import com.iam.server.api.dto.RoleDto;
import com.iam.server.api.dto.SetRolePermissionsRequest;
import com.iam.server.api.dto.UpdateRoleRequest;
import com.iam.server.rbac.service.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
  private final RoleService roleService;

  public RoleController(final RoleService roleService) {
    this.roleService = roleService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('sys:role:read')")
  public List<RoleDto> list() {
    return roleService.list();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:role:read')")
  public RoleDto get(@PathVariable("id") final String id) {
    return roleService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('sys:role:write')")
  public RoleDto create(@Valid @RequestBody final CreateRoleRequest req) {
    return roleService.create(req);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:role:write')")
  public RoleDto update(@PathVariable("id") final String id, @Valid @RequestBody final UpdateRoleRequest req) {
    return roleService.update(id, req);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:role:write')")
  public void delete(@PathVariable("id") final String id) {
    roleService.delete(id);
  }

  @PutMapping("/{id}/permissions")
  @PreAuthorize("hasAuthority('sys:role:write')")
  public void setPermissions(@PathVariable("id") final String id,
                             @Valid @RequestBody final SetRolePermissionsRequest req) {
    roleService.setPermissions(id, req);
  }
}

