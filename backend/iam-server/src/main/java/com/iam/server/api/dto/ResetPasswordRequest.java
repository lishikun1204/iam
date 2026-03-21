package com.iam.server.api.dto;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {
  @NotBlank
  private String newPassword;

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(final String newPassword) {
    this.newPassword = newPassword;
  }
}

