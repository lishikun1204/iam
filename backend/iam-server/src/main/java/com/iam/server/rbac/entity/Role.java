package com.iam.server.rbac.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "sys_role")
public class Role extends BaseEntity {
  @Column(name = "code", nullable = false, length = 64)
  private String code;

  @Column(name = "name", nullable = false, length = 64)
  private String name;

  @Column(name = "level_num", nullable = false)
  private int levelNum;

  @Enumerated(EnumType.STRING)
  @Column(name = "data_scope", nullable = false, length = 32)
  private DataScopeType dataScope;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 16)
  private RoleStatus status;

  @ManyToMany
  @JoinTable(
      name = "sys_role_permission",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  private Set<Permission> permissions = new LinkedHashSet<>();

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

  public Set<Permission> getPermissions() {
    return permissions != null ? Set.copyOf(permissions) : Set.of();
  }

  public void setPermissions(final Set<Permission> permissions) {
    this.permissions = permissions != null ? new java.util.LinkedHashSet<>(permissions) : null;
  }
}

