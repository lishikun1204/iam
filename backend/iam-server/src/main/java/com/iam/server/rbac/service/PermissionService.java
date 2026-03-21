package com.iam.server.rbac.service;

import com.iam.common.api.ApiErrorCode;
import com.iam.common.api.ApiException;
import com.iam.server.api.dto.CreatePermissionRequest;
import com.iam.server.api.dto.PermissionDto;
import com.iam.server.api.dto.UpdatePermissionRequest;
import com.iam.server.rbac.entity.Permission;
import com.iam.server.rbac.repo.PermissionRepository;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {
  private final PermissionRepository permissionRepository;

  public PermissionService(final PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  public List<PermissionDto> list() {
    return permissionRepository.findAll().stream().map(this::toDto).toList();
  }

  @Cacheable(cacheNames = "rbac:permission", key = "#id")
  public PermissionDto get(final String id) {
    Permission p = permissionRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "权限不存在"));
    return toDto(p);
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:permission", "rbac:userAuthorities"}, allEntries = true)
  public PermissionDto create(final CreatePermissionRequest req) {
    if (permissionRepository.existsByCode(req.getCode())) {
      throw new ApiException(ApiErrorCode.CONFLICT, "权限编码已存在");
    }
    Permission p = new Permission();
    p.setCode(req.getCode());
    p.setName(req.getName());
    p.setType(req.getType());
    p.setUrl(req.getUrl());
    p.setHttpMethod(req.getHttpMethod());
    p.setParentId(req.getParentId());
    p.setSortNum(req.getSortNum());
    p.setStatus(req.getStatus());
    return toDto(permissionRepository.save(p));
  }

  @Transactional
  @CachePut(cacheNames = "rbac:permission", key = "#id")
  @CacheEvict(cacheNames = {"rbac:userAuthorities"}, allEntries = true)
  public PermissionDto update(final String id, final UpdatePermissionRequest req) {
    Permission p = permissionRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "权限不存在"));
    p.setName(req.getName());
    p.setType(req.getType());
    p.setUrl(req.getUrl());
    p.setHttpMethod(req.getHttpMethod());
    p.setParentId(req.getParentId());
    p.setSortNum(req.getSortNum());
    p.setStatus(req.getStatus());
    return toDto(permissionRepository.save(p));
  }

  private PermissionDto toDto(final Permission p) {
    PermissionDto dto = new PermissionDto();
    dto.setId(p.getId());
    dto.setCode(p.getCode());
    dto.setName(p.getName());
    dto.setType(p.getType());
    dto.setUrl(p.getUrl());
    dto.setHttpMethod(p.getHttpMethod());
    dto.setParentId(p.getParentId());
    dto.setSortNum(p.getSortNum());
    dto.setStatus(p.getStatus());
    return dto;
  }
}

