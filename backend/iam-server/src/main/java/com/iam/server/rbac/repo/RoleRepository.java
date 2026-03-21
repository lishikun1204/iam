package com.iam.server.rbac.repo;

import com.iam.server.rbac.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
  Optional<Role> findByCode(String code);
  boolean existsByCode(String code);

  @EntityGraph(attributePaths = {"permissions"})
  Optional<Role> findWithPermissionsById(String id);
}

