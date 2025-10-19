package org.dimchik.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dimchik.common.request.LoginRequest;
import org.dimchik.dto.LoginDTO;
import org.dimchik.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    private static final String LOGIN_URL = "/api/v1/login";
    private static final String LOGOUT_URL = "/api/v1/logout";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(securityService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void loginReturnCorrectJson() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("securePass");

        LoginDTO expectedResponse = new LoginDTO("uuid-12345", "John");

        when(securityService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

    }

    @Test
    void logoutReturnCorrectJson() throws Exception {
        String bearer = "uuid-12345";

        mockMvc.perform(delete(LOGOUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer))
                .andExpect(status().isOk());
    }

}