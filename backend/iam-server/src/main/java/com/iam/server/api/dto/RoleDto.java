package com.iam.server.api.dto;

import com.iam.server.rbac.entity.DataScopeType;
import com.iam.server.rbac.entity.RoleStatus;
import java.util.Set;

public class RoleDto {
  private String id;
  private String code;
  private String name;
  private int levelNum;
  private DataScopeType dataScope;
  private RoleStatus status;
  private Set<String> permissionIds;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public int getLevelNum() {
    return levelNum;
  }

  public void setLevelNum(final int levelNum) {
    this.levelNum = levelNum;
  }

  public DataScopeType getDataScope() {
    return dataScope;
  }

  public void setDataScope(final DataScopeType dataScope) {
    this.dataScope = dataScope;
  }

  public RoleStatus getStatus() {
    return status;
  }

  public void setStatus(final RoleStatus status) {
    this.status = status;
  }

  public Set<String> getPermissionIds() {
    return permissionIds != null ? Set.copyOf(permissionIds) : Set.of();
  }

  public void setPermissionIds(final Set<String> permissionIds) {
    this.permissionIds = permissionIds != null ? new java.util.HashSet<>(permissionIds) : null;
  }
}

