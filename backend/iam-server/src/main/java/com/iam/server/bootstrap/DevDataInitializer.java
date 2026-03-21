package com.iam.server.bootstrap;

import com.iam.server.rbac.entity.DataScopeType;
import com.iam.server.rbac.entity.Department;
import com.iam.server.rbac.entity.Permission;
import com.iam.server.rbac.entity.PermissionStatus;
import com.iam.server.rbac.entity.PermissionType;
import com.iam.server.rbac.entity.Role;
import com.iam.server.rbac.entity.RoleStatus;
import com.iam.server.rbac.entity.User;
import com.iam.server.rbac.entity.UserStatus;
import com.iam.server.rbac.repo.DepartmentRepository;
import com.iam.server.rbac.repo.PermissionRepository;
import com.iam.server.rbac.repo.RoleRepository;
import com.iam.server.rbac.repo.UserRepository;
import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "dev-noredis", "test"})
public class DevDataInitializer implements CommandLineRunner {
  private final DepartmentRepository departmentRepository;
  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public DevDataInitializer(final DepartmentRepository departmentRepository,
                            final PermissionRepository permissionRepository,
                            final RoleRepository roleRepository,
                            final UserRepository userRepository,
                            final PasswordEncoder passwordEncoder) {
    this.departmentRepository = departmentRepository;
    this.permissionRepository = permissionRepository;
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(final String... args) {
    Department root = ensureRootDept();
    List<Permission> permissions = ensurePermissions();
    Role adminRole = ensureAdminRole(permissions);
    ensureAdminUser(root, adminRole);
  }

  private Department ensureRootDept() {
    if (departmentRepository.existsByCode("ROOT")) {
      return departmentRepository.findAll().stream().filter(d -> "ROOT".equals(d.getCode())).findFirst().orElseThrow();
    }
    Department dept = new Department();
    dept.setCode("ROOT");
    dept.setName("总部");
    dept.setParentId(null);
    dept.setSortNum(0);
    dept.setLeader("admin");
    dept.setPhone("00000000000");
    return departmentRepository.save(dept);
  }

  private List<Permission> ensurePermissions() {
    ensurePermission("sys:menu:console", "控制台", PermissionType.MENU, null, null, null, 1);
    ensurePermission("sys:user:read", "用户-查询", PermissionType.API, "/api/users", "GET", null, 10);
    ensurePermission("sys:user:write", "用户-写入", PermissionType.API, "/api/users", "POST", null, 11);
    ensurePermission("sys:role:read", "角色-查询", PermissionType.API, "/api/roles", "GET", null, 20);
    ensurePermission("sys:role:write", "角色-写入", PermissionType.API, "/api/roles", "POST", null, 21);
    ensurePermission("sys:perm:read", "权限-查询", PermissionType.API, "/api/permissions", "GET", null, 30);
    ensurePermission("sys:perm:write", "权限-写入", PermissionType.API, "/api/permissions", "POST", null, 31);
    ensurePermission("sys:dept:read", "部门-查询", PermissionType.API, "/api/depts", "GET", null, 40);
    ensurePermission("sys:dept:write", "部门-写入", PermissionType.API, "/api/depts", "POST", null, 41);
    return permissionRepository.findAll();
  }

  private Permission ensurePermission(final String code,
                                      final String name,
                                      final PermissionType type,
                                      final String url,
                                      final String method,
                                      final String parentId,
                                      final int sortNum) {
    return permissionRepository.findByCode(code).orElseGet(() -> {
      Permission p = new Permission();
      p.setCode(code);
      p.setName(name);
      p.setType(type);
      p.setUrl(url);
      p.setHttpMethod(method);
      p.setParentId(parentId);
      p.setSortNum(sortNum);
      p.setStatus(PermissionStatus.ENABLED);
      return permissionRepository.save(p);
    });
  }

  private Role ensureAdminRole(final List<Permission> permissions) {
    Role role = roleRepository.findByCode("ADMIN").orElseGet(() -> {
      Role r = new Role();
      r.setCode("ADMIN");
      r.setName("系统管理员");
      r.setLevelNum(1);
      r.setDataScope(DataScopeType.ALL);
      r.setStatus(RoleStatus.ENABLED);
      return roleRepository.save(r);
    });
    role.setPermissions(Set.copyOf(permissions));
    return roleRepository.save(role);
  }

  private void ensureAdminUser(final Department dept, final Role role) {
    if (userRepository.existsByUsername("admin")) {
      return;
    }
    User user = new User();
    user.setUsername("admin");
    user.setFullName("管理员");
    user.setEmail("admin@example.com");
    user.setPhone("00000000000");
    user.setAvatar(null);
    user.setStatus(UserStatus.ENABLED);
    user.setDeptId(dept.getId());
    user.setPasswordHash(passwordEncoder.encode("Admin@123"));
    user.setRoles(Set.of(role));
    userRepository.save(user);
  }
}

