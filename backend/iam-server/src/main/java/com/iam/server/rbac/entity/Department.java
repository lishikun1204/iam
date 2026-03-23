package com.iam.server.rbac.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_dept")
public class Department extends BaseEntity {
  @Column(name = "code", nullable = false, length = 64)
  private String code;

  @Column(name = "name", nullable = false, length = 128)
  private String name;

  @Column(name = "parent_id", length = 36)
  private String parentId;

  @Column(name = "sort_num", nullable = false)
  private int sortNum;

  @Column(name = "leader", length = 64)
  private String leader;

  @Column(name = "phone", length = 32)
  private String phone;

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
