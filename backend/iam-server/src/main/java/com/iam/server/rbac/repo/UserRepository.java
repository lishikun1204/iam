package com.iam.server.rbac.repo;

import com.iam.server.rbac.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);

  @EntityGraph(attributePaths = {"roles", "roles.permissions"})
  Optional<User> findWithRolesByUsername(String username);
}

