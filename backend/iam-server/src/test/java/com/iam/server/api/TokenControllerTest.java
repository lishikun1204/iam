package com.iam.server.api;

import com.iam.server.security.TokenRevocationService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(TokenController.class)
@Import({com.iam.server.web.ApiResponseAdvice.class, com.iam.server.web.GlobalExceptionHandler.class})
class TokenControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private JwtDecoder jwtDecoder;

  @MockBean
  private TokenRevocationService tokenRevocationService;

  @Test
  @WithMockUser(authorities = {"sys:menu:console"})
  void revoke_shouldReturnOk() throws Exception {
    Jwt jwt = Jwt.withTokenValue("t")
        .header("alg", "none")
        .claim("jti", "j1")
        .subject("u")
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(60))
        .build();
    Mockito.when(jwtDecoder.decode("token")).thenReturn(jwt);

    mvc.perform(post("/api/tokens/revoke")
            .with(csrf())
            .contentType("application/json")
            .content("{\"accessToken\":\"token\"}"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"sys:menu:console"})
  void revoke_shouldReturnBadRequestWhenInvalid() throws Exception {
    Mockito.when(jwtDecoder.decode("bad")).thenThrow(new RuntimeException("bad"));
    mvc.perform(post("/api/tokens/revoke")
            .with(csrf())
            .contentType("application/json")
            .content("{\"accessToken\":\"bad\"}"))
        .andExpect(status().isBadRequest());
  }
}
