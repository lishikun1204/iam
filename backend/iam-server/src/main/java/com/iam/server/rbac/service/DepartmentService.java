package com.iam.server.rbac.service;

import com.iam.common.api.ApiErrorCode;
import com.iam.common.api.ApiException;
import com.iam.server.api.dto.CreateDepartmentRequest;
import com.iam.server.api.dto.DepartmentDto;
import com.iam.server.api.dto.UpdateDepartmentRequest;
import com.iam.server.rbac.entity.Department;
import com.iam.server.rbac.repo.DepartmentRepository;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {
  private final DepartmentRepository departmentRepository;

  public DepartmentService(final DepartmentRepository departmentRepository) {
    this.departmentRepository = departmentRepository;
  }

  public List<DepartmentDto> list() {
    return departmentRepository.findAll().stream().map(this::toDto).toList();
  }

  @Cacheable(cacheNames = "rbac:dept", key = "#id")
  public DepartmentDto get(final String id) {
    Department d = departmentRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "部门不存在"));
    return toDto(d);
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:dept"}, allEntries = true)
  public DepartmentDto create(final CreateDepartmentRequest req) {
    if (departmentRepository.existsByCode(req.getCode())) {
      throw new ApiException(ApiErrorCode.CONFLICT, "部门编码已存在");
    }
    Department d = new Department();
    d.setCode(req.getCode());
    d.setName(req.getName());
    d.setParentId(req.getParentId());
    d.setSortNum(req.getSortNum());
    d.setLeader(req.getLeader());
    d.setPhone(req.getPhone());
    return toDto(departmentRepository.save(d));
  }

  @Transactional
  @CachePut(cacheNames = "rbac:dept", key = "#id")
  public DepartmentDto update(final String id, final UpdateDepartmentRequest req) {
    Department d = departmentRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "部门不存在"));
    d.setName(req.getName());
    d.setParentId(req.getParentId());
    d.setSortNum(req.getSortNum());
    d.setLeader(req.getLeader());
    d.setPhone(req.getPhone());
    return toDto(departmentRepository.save(d));
  }

  @Transactional
  @CacheEvict(cacheNames = {"rbac:dept"}, allEntries = true)
  public void delete(final String id) {
    Department d = departmentRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND, "部门不存在"));
    if ("ROOT".equals(d.getCode())) {
      throw new ApiException(ApiErrorCode.FORBIDDEN, "不允许删除根部门");
    }
    departmentRepository.delete(d);
  }

  private DepartmentDto toDto(final Department d) {
    DepartmentDto dto = new DepartmentDto();
    dto.setId(d.getId());
    dto.setCode(d.getCode());
    dto.setName(d.getName());
    dto.setParentId(d.getParentId());
    dto.setSortNum(d.getSortNum());
    dto.setLeader(d.getLeader());
    dto.setPhone(d.getPhone());
    return dto;
  }
}

