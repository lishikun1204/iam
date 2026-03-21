package com.iam.server.api.dto;

import com.iam.server.rbac.entity.UserStatus;

public class UserDto {
  private String id;
  private String username;
  private String fullName;
  private String email;
  private String phone;
  private String avatar;
  private UserStatus status;
  private String deptId;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(final String avatar) {
    this.avatar = avatar;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(final UserStatus status) {
    this.status = status;
  }

  public String getDeptId() {
    return deptId;
  }

  public void setDeptId(final String deptId) {
    this.deptId = deptId;
  }
}

