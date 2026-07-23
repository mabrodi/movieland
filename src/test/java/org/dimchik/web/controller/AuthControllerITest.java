package org.dimchik.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerITest extends AbstractBaseITest {

    //ENDPOINTS
    private static final String API_URL = "/api/v1";

    //REQUEST JSON
    private static final String REQUEST_LOGIN_JSON = "request/auth/login.json";

    @Test
    @DataSet(value = "datasets/user.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void loginShouldReturnTokenWhenValidCredentials() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(post(API_URL + "/login")
                        .content(readJson(REQUEST_LOGIN_JSON))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/user.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void loginShouldReturn401WhenUserNotFound() throws Exception {
        String json = """
                {
                  "email": "test@test.com",
                  "password": "1234"
                }
                """;

        SQLStatementCountValidator.reset();
        mockMvc.perform(post(API_URL + "/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "datasets/user.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void refreshShouldReturnNewToken() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(post(API_URL + "/refresh")
                        .header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertSelectCount(0);
    }

    @Test
    void refreshShouldReturn403WhenInvalidToken() throws Exception {
        mockMvc.perform(post(API_URL + "/refresh")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DataSet(value = "datasets/user.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void logoutShouldReturnNoContent() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(delete(API_URL + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertSelectCount(0);
    }


    @Test
    void logoutShouldBlacklistToken() throws Exception {
        String token = adminToken();
        //Logout add to blacklist
        mockMvc.perform(delete(API_URL + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Refresh с blacklisted token — should return 403
        mockMvc.perform(post(API_URL + "/refresh")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
