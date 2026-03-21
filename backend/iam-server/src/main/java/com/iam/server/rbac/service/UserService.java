package com.iam.server.rbac.service;

import com.iam.common.api.ApiErrorCode;
import com.iam.common.api.ApiException;
import com.iam.server.api.dto.AssignRolesRequest;
import com.iam.server.api.dto.CreateUserRequest;
import com.iam.server.api.dto.ResetPasswordRequest;
import com.iam.server.api.dto.UpdateUserRequest;
import com.iam.server.api.dto.UserDto;
import com.iam.server.rbac.entity.Role;
import com.iam.server.rbac.entity.User;
import com.iam.server.rbac.repo.RoleRepository;
import com.iam.server.rbac.repo.UserRepository;
import java.util.List;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(final UserRepository userRepository,
                     final RoleRepository roleRepository,
                     final PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<UserDto> list() {
    return userRepository.findAll().stream().map(this::toDto).toList();
  }

  @Cacheable(cacheNames = "rbac:user", key = "#id")
  public UserDto get(final String id) {
    User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "用户不存在"));
    return toDto(user);
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:userAuthorities"}, allEntries = true)
  public UserDto create(final CreateUserRequest req) {
    if (userRepository.existsByUsername(req.getUsername())) {
      throw new ApiException(ApiErrorCode.CONFLICT, "登录名已存在");
    }
    User user = new User();
    user.setUsername(req.getUsername());
    user.setFullName(req.getFullName());
    user.setEmail(req.getEmail());
    user.setPhone(req.getPhone());
    user.setAvatar(req.getAvatar());
    user.setDeptId(req.getDeptId());
    user.setStatus(req.getStatus());
    user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    User saved = userRepository.save(user);
    return toDto(saved);
  }

  @Transactional
  @CachePut(cacheNames = "rbac:user", key = "#id")
  @CacheEvict(cacheNames = {"rbac:userAuthorities"}, allEntries = true)
  public UserDto update(final String id, final UpdateUserRequest req) {
    User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "用户不存在"));
    user.setFullName(req.getFullName());
    user.setEmail(req.getEmail());
    user.setPhone(req.getPhone());
    user.setAvatar(req.getAvatar());
    user.setDeptId(req.getDeptId());
    user.setStatus(req.getStatus());
    return toDto(userRepository.save(user));
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:user", "rbac:userAuthorities"}, allEntries = true)
  public void resetPassword(final String id, final ResetPasswordRequest req) {
    User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "用户不存在"));
    user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
    userRepository.save(user);
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:user", "rbac:userAuthorities"}, allEntries = true)
  public void assignRoles(final String userId, final AssignRolesRequest req) {
    User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "用户不存在"));
    List<Role> roles = roleRepository.findAllById(req.getRoleIds());
    if (roles.size() != req.getRoleIds().size()) {
      throw new ApiException(ApiErrorCode.NOT_FOUND, "部分角色不存在");
    }
    user.setRoles(Set.copyOf(roles));
    userRepository.save(user);
  }

  private UserDto toDto(final User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setFullName(user.getFullName());
    dto.setEmail(user.getEmail());
    dto.setPhone(user.getPhone());
    dto.setAvatar(user.getAvatar());
    dto.setStatus(user.getStatus());
    dto.setDeptId(user.getDeptId());
    return dto;
  }
}

