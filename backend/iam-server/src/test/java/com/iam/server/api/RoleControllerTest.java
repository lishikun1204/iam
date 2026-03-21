package com.iam.server.api;

import com.iam.server.rbac.service.RoleService;
import com.iam.server.api.dto.RoleDto;
import com.iam.server.rbac.entity.DataScopeType;
import com.iam.server.rbac.entity.RoleStatus;
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

@WebMvcTest(RoleController.class)
@Import(com.iam.server.web.ApiResponseAdvice.class)
class RoleControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private RoleService roleService;

  @Test
  @WithMockUser(authorities = {"sys:role:read"})
  void list_shouldReturnOk() throws Exception {
    Mockito.when(roleService.list()).thenReturn(List.of());
    mvc.perform(get("/api/roles")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"sys:role:write"})
  void create_shouldReturnOk() throws Exception {
    RoleDto dto = new RoleDto();
    dto.setId("1");
    dto.setCode("R");
    dto.setName("Role");
    dto.setLevelNum(1);
    dto.setDataScope(DataScopeType.ALL);
    dto.setStatus(RoleStatus.ENABLED);
    Mockito.when(roleService.create(Mockito.any())).thenReturn(dto);

    mvc.perform(post("/api/roles")
            .with(csrf())
            .contentType("application/json")
            .content("{\"code\":\"R\",\"name\":\"Role\",\"levelNum\":1,\"dataScope\":\"ALL\",\"status\":\"ENABLED\"}"))
        .andExpect(status().isOk());
  }
}
