package org.dimchik.web.controller;

import org.dimchik.security.AuthFilter;
import org.dimchik.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest extends AbstractBaseTest {
    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AuthFilter authFilter;

    private String loginRequestJson;
    private String loginResponseJson;

    @BeforeEach
    void setUp() {
        loginRequestJson = readJson("authLoginRequest.json");
        loginResponseJson = readJson("authLoginResponse.json");
    }

    @Test
    void loginShouldReturnTokenWhenValidCredentials() throws Exception {
        var tokenResponse = new org.dimchik.dto.response.TokenResponse("jwt.token.here", "Bearer");
        when(authService.login(any())).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(loginResponseJson));

        verify(authService).login(any());
    }

    @Test
    void loginShouldReturn400WhenEmailInvalid() throws Exception {
        String invalidEmailJson = """
                {"email": "not-an-email", "password": "password123"}
                """;

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void refreshShouldReturnNewToken() throws Exception {
        var tokenResponse = new org.dimchik.dto.response.TokenResponse("new.jwt.token", "Bearer");
        when(authService.refresh("Bearer old.token")).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/refresh")
                        .header("Authorization", "Bearer old.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new.jwt.token"));

        verify(authService).refresh("Bearer old.token");
    }

    @Test
    void logoutShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/logout")
                        .header("Authorization", "Bearer some.token"))
                .andExpect(status().isNoContent());

        verify(authService).logout("Bearer some.token");
    }
}
