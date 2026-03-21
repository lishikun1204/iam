package com.iam.server.api;

import com.iam.server.rbac.service.PermissionService;
import com.iam.server.api.dto.PermissionDto;
import com.iam.server.rbac.entity.PermissionStatus;
import com.iam.server.rbac.entity.PermissionType;
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

@WebMvcTest(PermissionController.class)
@Import(com.iam.server.web.ApiResponseAdvice.class)
class PermissionControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private PermissionService permissionService;

  @Test
  @WithMockUser(authorities = {"sys:perm:read"})
  void list_shouldReturnOk() throws Exception {
    Mockito.when(permissionService.list()).thenReturn(List.of());
    mvc.perform(get("/api/permissions")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"sys:perm:write"})
  void create_shouldReturnOk() throws Exception {
    PermissionDto dto = new PermissionDto();
    dto.setId("1");
    dto.setCode("c");
    dto.setName("n");
    dto.setType(PermissionType.API);
    dto.setSortNum(0);
    dto.setStatus(PermissionStatus.ENABLED);
    Mockito.when(permissionService.create(Mockito.any())).thenReturn(dto);

    mvc.perform(post("/api/permissions")
            .with(csrf())
            .contentType("application/json")
            .content("{\"code\":\"c\",\"name\":\"n\",\"type\":\"API\",\"sortNum\":0,\"status\":\"ENABLED\"}"))
        .andExpect(status().isOk());
  }
}
