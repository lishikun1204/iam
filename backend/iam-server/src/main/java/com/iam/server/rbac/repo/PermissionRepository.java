package com.iam.server.rbac.repo;

import com.iam.server.rbac.entity.Permission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
  Optional<Permission> findByCode(String code);
  boolean existsByCode(String code);
}

