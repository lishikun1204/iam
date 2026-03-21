package com.iam.server.rbac.service;

import com.iam.common.api.ApiException;
import com.iam.server.api.dto.CreateUserRequest;
import com.iam.server.api.dto.ResetPasswordRequest;
import com.iam.server.api.dto.UpdateUserRequest;
import com.iam.server.rbac.entity.User;
import com.iam.server.rbac.entity.UserStatus;
import com.iam.server.rbac.entity.Role;
import com.iam.server.rbac.entity.DataScopeType;
import com.iam.server.rbac.entity.RoleStatus;
import com.iam.server.api.dto.AssignRolesRequest;
import com.iam.server.rbac.repo.RoleRepository;
import com.iam.server.rbac.repo.UserRepository;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {
  @Test
  void create_shouldSaveAndReturnDto() {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    Mockito.when(userRepository.existsByUsername("u")).thenReturn(false);
    Mockito.when(passwordEncoder.encode("p")).thenReturn("enc");
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> {
      User u = inv.getArgument(0);
      u.setId("1");
      return u;
    });

    UserService service = new UserService(userRepository, roleRepository, passwordEncoder);
    CreateUserRequest req = new CreateUserRequest();
    req.setUsername("u");
    req.setFullName("U");
    req.setStatus(UserStatus.ENABLED);
    req.setPassword("p");

    Assertions.assertEquals("u", service.create(req).getUsername());
  }

  @Test
  void create_shouldThrowOnDuplicateUsername() {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    Mockito.when(userRepository.existsByUsername("u")).thenReturn(true);

    UserService service = new UserService(userRepository, roleRepository, passwordEncoder);
    CreateUserRequest req = new CreateUserRequest();
    req.setUsername("u");
    req.setFullName("U");
    req.setStatus(UserStatus.ENABLED);
    req.setPassword("p");

    Assertions.assertThrows(ApiException.class, () -> service.create(req));
  }

  @Test
  void update_shouldThrowWhenNotFound() {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    Mockito.when(userRepository.findById("1")).thenReturn(Optional.empty());

    UserService service = new UserService(userRepository, roleRepository, passwordEncoder);
    UpdateUserRequest req = new UpdateUserRequest();
    req.setFullName("U");
    req.setStatus(UserStatus.ENABLED);

    Assertions.assertThrows(ApiException.class, () -> service.update("1", req));
  }

  @Test
  void update_shouldSaveAndReturnDto() {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    User user = new User();
    user.setId("1");
    user.setUsername("u");
    user.setFullName("old");
    user.setStatus(UserStatus.ENABLED);
    user.setPasswordHash("hash");
    Mockito.when(userRepository.findById("1")).thenReturn(Optional.of(user));
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    UserService service = new UserService(userRepository, roleRepository, passwordEncoder);
    UpdateUserRequest req = new UpdateUserRequest();
    req.setFullName("new");
    req.setEmail("a@b.com");
    req.setPhone("1");
    req.setStatus(UserStatus.ENABLED);

    Assertions.assertEquals("new", service.update("1", req).getFullName());
  }

  @Test
  void resetPassword_shouldEncodeAndSave() {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    User user = new User();
    user.setId("1");
    user.setUsername("u");
    user.setFullName("U");
    user.setStatus(UserStatus.ENABLED);
    user.setPasswordHash("old");
    Mockito.when(userRepository.findById("1")).thenReturn(Optional.of(user));
    Mockito.when(passwordEncoder.encode("new")).thenReturn("encoded");

    UserService service = new UserService(userRepository, roleRepository, passwordEncoder);
    ResetPasswordRequest req = new ResetPasswordRequest();
    req.setNewPassword("new");
    service.resetPassword("1", req);

    Mockito.verify(userRepository).save(Mockito.argThat(u -> "encoded".equals(u.getPasswordHash())));
  }

  @Test
  void assignRoles_shouldSaveUser() {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    User user = new User();
    user.setId("u1");
    user.setUsername("u");
    user.setFullName("U");
    user.setStatus(UserStatus.ENABLED);
    user.setPasswordHash("hash");
    Mockito.when(userRepository.findById("u1")).thenReturn(Optional.of(user));

    Role role = new Role();
    role.setId("r1");
    role.setCode("R");
    role.setName("Role");
    role.setLevelNum(1);
    role.setDataScope(DataScopeType.ALL);
    role.setStatus(RoleStatus.ENABLED);
    Mockito.when(roleRepository.findAllById(Set.of("r1"))).thenReturn(List.of(role));
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    UserService service = new UserService(userRepository, roleRepository, passwordEncoder);
    AssignRolesRequest req = new AssignRolesRequest();
    req.setRoleIds(Set.of("r1"));
    service.assignRoles("u1", req);

    Mockito.verify(userRepository).save(Mockito.argThat(u -> u.getRoles().size() == 1));
  }
}
