package com.iam.server.api.dto;

import com.iam.server.rbac.entity.PermissionStatus;
import com.iam.server.rbac.entity.PermissionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdatePermissionRequest {
  @NotBlank
  private String name;

  @NotNull
  private PermissionType type;

  private String url;
  private String httpMethod;
  private String parentId;

  @Min(0)
  private int sortNum;

  @NotNull
  private PermissionStatus status;

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

