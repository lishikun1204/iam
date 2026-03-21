package com.iam.server.api;

import com.iam.server.rbac.service.UserService;
import com.iam.server.api.dto.UserDto;
import com.iam.server.rbac.entity.UserStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
@Import(com.iam.server.web.ApiResponseAdvice.class)
class UserControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserService userService;

  @Test
  @WithMockUser(authorities = {"sys:user:read"})
  void list_shouldReturnOk() throws Exception {
    Mockito.when(userService.list()).thenReturn(List.of());
    mvc.perform(get("/api/users")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"sys:user:write"})
  void create_shouldReturnOk() throws Exception {
    UserDto dto = new UserDto();
    dto.setId("1");
    dto.setUsername("u");
    dto.setFullName("U");
    dto.setStatus(UserStatus.ENABLED);
    Mockito.when(userService.create(Mockito.any())).thenReturn(dto);

    mvc.perform(post("/api/users")
            .with(csrf())
            .contentType("application/json")
            .content("{\"username\":\"u\",\"fullName\":\"U\",\"status\":\"ENABLED\",\"password\":\"p\"}"))
        .andExpect(status().isOk());
  }
}
