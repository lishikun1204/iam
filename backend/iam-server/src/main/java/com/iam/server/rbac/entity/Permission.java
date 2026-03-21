package com.iam.server.rbac.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_permission")
public class Permission extends BaseEntity {
  @Column(name = "code", nullable = false, length = 128)
  private String code;

  @Column(name = "name", nullable = false, length = 128)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 16)
  private PermissionType type;

  @Column(name = "url", length = 512)
  private String url;

  @Column(name = "http_method", length = 16)
  private String httpMethod;

  @Column(name = "parent_id", length = 36)
  private String parentId;

  @Column(name = "sort_num", nullable = false)
  private int sortNum;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 16)
  private PermissionStatus status;

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

  public PermissionType getType() {
    return type;
  }

  public void setType(final PermissionType type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(final String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(final String parentId) {
    this.parentId = parentId;
  }

  public int getSortNum() {
    return sortNum;
  }

  public void setSortNum(final int sortNum) {
    this.sortNum = sortNum;
  }

  public PermissionStatus getStatus() {
    return status;
  }

  public void setStatus(final PermissionStatus status) {
    this.status = status;
  }
}

