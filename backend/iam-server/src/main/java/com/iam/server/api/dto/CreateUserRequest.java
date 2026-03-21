package com.iam.server.api.dto;

import com.iam.server.rbac.entity.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {
  @NotBlank
  private String username;

  @NotBlank
  private String fullName;

  private String email;
  private String phone;
  private String avatar;
  private String deptId;

  @NotNull
  private UserStatus status;

  @NotBlank
  private String password;

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

  public String getDeptId() {
    return deptId;
  }

  public void setDeptId(final String deptId) {
    this.deptId = deptId;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(final UserStatus status) {
    this.status = status;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }
}

