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
@Table(name = "sys_user")
public class User extends BaseEntity {
  @Column(name = "username", nullable = false, length = 64)
  private String username;

  @Column(name = "full_name", nullable = false, length = 64)
  private String fullName;

  @Column(name = "email", length = 128)
  private String email;

  @Column(name = "phone", length = 32)
  private String phone;

  @Column(name = "avatar", length = 512)
  private String avatar;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 16)
  private UserStatus status;

  @Column(name = "dept_id", length = 36)
  private String deptId;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @ManyToMany
  @JoinTable(
      name = "sys_user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<Role> roles = new LinkedHashSet<>();

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

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(final String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Set<Role> getRoles() {
    return roles != null ? Set.copyOf(roles) : Set.of();
  }

  public void setRoles(final Set<Role> roles) {
    this.roles = roles != null ? new java.util.LinkedHashSet<>(roles) : null;
  }
}

