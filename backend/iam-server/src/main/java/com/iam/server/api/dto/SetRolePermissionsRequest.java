package com.iam.server.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public class SetRolePermissionsRequest {
  @NotNull
  private Set<String> permissionIds;

  public Set<String> getPermissionIds() {
    return permissionIds != null ? Set.copyOf(permissionIds) : Set.of();
  }

  public void setPermissionIds(final Set<String> permissionIds) {
    this.permissionIds = permissionIds != null ? new java.util.HashSet<>(permissionIds) : null;
  }
}

