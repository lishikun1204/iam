package com.iam.server.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public class AssignRolesRequest {
  @NotEmpty
  private Set<String> roleIds;

  public Set<String> getRoleIds() {
    return roleIds;
  }

  public void setRoleIds(final Set<String> roleIds) {
    this.roleIds = roleIds;
  }
}

