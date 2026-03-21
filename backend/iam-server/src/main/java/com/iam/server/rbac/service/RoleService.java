package com.iam.server.rbac.service;

import com.iam.common.api.ApiErrorCode;
import com.iam.common.api.ApiException;
import com.iam.server.api.dto.CreateRoleRequest;
import com.iam.server.api.dto.RoleDto;
import com.iam.server.api.dto.SetRolePermissionsRequest;
import com.iam.server.api.dto.UpdateRoleRequest;
import com.iam.server.rbac.entity.Permission;
import com.iam.server.rbac.entity.Role;
import com.iam.server.rbac.repo.PermissionRepository;
import com.iam.server.rbac.repo.RoleRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  public RoleService(final RoleRepository roleRepository, final PermissionRepository permissionRepository) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
  }

  public List<RoleDto> list() {
    return roleRepository.findAll().stream().map(this::toDtoShallow).toList();
  }

  @Cacheable(cacheNames = "rbac:role", key = "#id")
  public RoleDto get(final String id) {
    Role role = roleRepository.findWithPermissionsById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "角色不存在"));
    return toDto(role);
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:role", "rbac:userAuthorities"}, allEntries = true)
  public RoleDto create(final CreateRoleRequest req) {
    if (roleRepository.existsByCode(req.getCode())) {
      throw new ApiException(ApiErrorCode.CONFLICT, "角色编码已存在");
    }
    Role role = new Role();
    role.setCode(req.getCode());
    role.setName(req.getName());
    role.setLevelNum(req.getLevelNum());
    role.setDataScope(req.getDataScope());
    role.setStatus(req.getStatus());
    if (req.getPermissionIds() != null && !req.getPermissionIds().isEmpty()) {
      List<Permission> perms = permissionRepository.findAllById(req.getPermissionIds());
      if (perms.size() != req.getPermissionIds().size()) {
        throw new ApiException(ApiErrorCode.NOT_FOUND, "部分权限不存在");
      }
      role.setPermissions(Set.copyOf(perms));
    }
    return toDto(roleRepository.save(role));
  }

  @Transactional
  @CachePut(cacheNames = "rbac:role", key = "#id")
  @CacheEvict(cacheNames = {"rbac:userAuthorities"}, allEntries = true)
  public RoleDto update(final String id, final UpdateRoleRequest req) {
    Role role = roleRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "角色不存在"));
    role.setName(req.getName());
    role.setLevelNum(req.getLevelNum());
    role.setDataScope(req.getDataScope());
    role.setStatus(req.getStatus());
    return toDto(roleRepository.save(role));
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:role", "rbac:userAuthorities"}, allEntries = true)
  public void delete(final String id) {
    Role role = roleRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "角色不存在"));
    if ("ADMIN".equals(role.getCode())) {
      throw new ApiException(ApiErrorCode.FORBIDDEN, "不允许删除内置角色");
    }
    roleRepository.delete(role);
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:role", "rbac:userAuthorities"}, allEntries = true)
  public void setPermissions(final String roleId, final SetRolePermissionsRequest req) {
    Role role = roleRepository.findWithPermissionsById(roleId)
        .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "角色不存在"));
    List<Permission> perms = permissionRepository.findAllById(req.getPermissionIds());
    if (perms.size() != req.getPermissionIds().size()) {
      throw new ApiException(ApiErrorCode.NOT_FOUND, "部分权限不存在");
    }
    role.setPermissions(Set.copyOf(perms));
    roleRepository.save(role);
  }

  private RoleDto toDtoShallow(final Role role) {
    RoleDto dto = new RoleDto();
    dto.setId(role.getId());
    dto.setCode(role.getCode());
    dto.setName(role.getName());
    dto.setLevelNum(role.getLevelNum());
    dto.setDataScope(role.getDataScope());
    dto.setStatus(role.getStatus());
    dto.setPermissionIds(null);
    return dto;
  }

  private RoleDto toDto(final Role role) {
    RoleDto dto = new RoleDto();
    dto.setId(role.getId());
    dto.setCode(role.getCode());
    dto.setName(role.getName());
    dto.setLevelNum(role.getLevelNum());
    dto.setDataScope(role.getDataScope());
    dto.setStatus(role.getStatus());
    dto.setPermissionIds(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()));
    return dto;
  }
}

