package com.iam.server.api;

import com.iam.server.api.dto.AssignRolesRequest;
import com.iam.server.api.dto.CreateUserRequest;
import com.iam.server.api.dto.ResetPasswordRequest;
import com.iam.server.api.dto.UpdateUserRequest;
import com.iam.server.api.dto.UserDto;
import com.iam.server.rbac.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('sys:user:read')")
  public List<UserDto> list() {
    return userService.list();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:user:read')")
  public UserDto get(@PathVariable("id") final String id) {
    return userService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('sys:user:write')")
  public UserDto create(@Valid @RequestBody final CreateUserRequest req) {
    return userService.create(req);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('sys:user:write')")
  public UserDto update(@PathVariable("id") final String id, @Valid @RequestBody final UpdateUserRequest req) {
    return userService.update(id, req);
  }

  @PostMapping("/{id}/reset-password")
  @PreAuthorize("hasAuthority('sys:user:write')")
  public void resetPassword(@PathVariable("id") final String id, @Valid @RequestBody final ResetPasswordRequest req) {
    userService.resetPassword(id, req);
  }

  @PutMapping("/{id}/roles")
  @PreAuthorize("hasAuthority('sys:user:write')")
  public void assignRoles(@PathVariable("id") final String id, @Valid @RequestBody final AssignRolesRequest req) {
    userService.assignRoles(id, req);
  }
}

