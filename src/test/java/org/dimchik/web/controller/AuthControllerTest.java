package org.dimchik.web.controller;

import org.dimchik.dto.response.TokenResponse;
import org.dimchik.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "security.jwt.secret=0123456789abcdef0123456789abcdef",
        "security.jwt.ttl-seconds=900",
        "security.jwt.blacklist.cleanup-cron=0 * * * * *"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void loginShouldReturnTokenWhenValidCredentials() throws Exception {
        TokenResponse tokenResponse = new TokenResponse("jwt.token.here", "Bearer");
        when(authService.login(any())).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "test@example.com", "password": "password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService).login(any());
    }

    @Test
    void loginShouldReturn400WhenEmailInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "not-an-email", "password": "password123"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void refreshShouldReturnNewToken() throws Exception {
        TokenResponse tokenResponse = new TokenResponse("new.jwt.token", "Bearer");
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
