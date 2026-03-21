package com.iam.server.rbac.repo;

import com.iam.server.rbac.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {
  boolean existsByCode(String code);
}

