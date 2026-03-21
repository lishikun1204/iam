package com.iam.server.api.dto;

public class DepartmentDto {
  private String id;
  private String code;
  private String name;
  private String parentId;
  private int sortNum;
  private String leader;
  private String phone;

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

