package com.iam.server.api;

import com.iam.server.rbac.service.RbacQueryService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeController.class)
@Import(com.iam.server.web.ApiResponseAdvice.class)
class MeControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private RbacQueryService rbacQueryService;

  @Test
  @WithMockUser(username = "admin")
  void me_shouldReturnOk() throws Exception {
    Mockito.when(rbacQueryService.getAuthorityCodesByUsername("admin")).thenReturn(Set.of("sys:user:read"));
    mvc.perform(get("/api/me"))
        .andExpect(status().isOk());
  }
}

