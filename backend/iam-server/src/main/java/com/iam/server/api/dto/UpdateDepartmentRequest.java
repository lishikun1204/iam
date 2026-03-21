package com.iam.server.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateDepartmentRequest {
  @NotBlank
  private String name;

  private String parentId;

  @NotNull
  @Min(0)
  private Integer sortNum;

  private String leader;
  private String phone;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(final String parentId) {
    this.parentId = parentId;
  }

  public Integer getSortNum() {
    return sortNum;
  }

  public void setSortNum(final Integer sortNum) {
    this.sortNum = sortNum;
  }

  public String getLeader() {
    return leader;
  }

  public void setLeader(final String leader) {
    this.leader = leader;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }
}

