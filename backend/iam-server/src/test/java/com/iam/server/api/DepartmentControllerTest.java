package com.iam.server.api;

import com.iam.server.rbac.service.DepartmentService;
import com.iam.server.api.dto.DepartmentDto;
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

@WebMvcTest(DepartmentController.class)
@Import(com.iam.server.web.ApiResponseAdvice.class)
class DepartmentControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private DepartmentService departmentService;

  @Test
  @WithMockUser(authorities = {"sys:dept:read"})
  void list_shouldReturnOk() throws Exception {
    Mockito.when(departmentService.list()).thenReturn(List.of());
    mvc.perform(get("/api/depts")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"sys:dept:write"})
  void create_shouldReturnOk() throws Exception {
    DepartmentDto dto = new DepartmentDto();
    dto.setId("1");
    dto.setCode("D");
    dto.setName("部门");
    dto.setSortNum(0);
    Mockito.when(departmentService.create(Mockito.any())).thenReturn(dto);

    mvc.perform(post("/api/depts")
            .with(csrf())
            .contentType("application/json")
            .content("{\"code\":\"D\",\"name\":\"部门\",\"sortNum\":0}"))
        .andExpect(status().isOk());
  }
}
