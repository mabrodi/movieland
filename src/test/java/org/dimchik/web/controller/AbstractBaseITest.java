package org.dimchik.web.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.spring.api.DBRider;
import com.redis.testcontainers.RedisContainer;
import org.dimchik.config.DataSourceWrapper;
import org.dimchik.dto.JwtUserDetails;
import org.dimchik.enums.Role;
import org.dimchik.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@DBRider
@DBUnit(schema = "public", caseInsensitiveStrategy = Orthography.LOWERCASE)
@AutoConfigureMockMvc
@Import(DataSourceWrapper.class)
public class AbstractBaseITest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.12");

    @Container
    @ServiceConnection
    private static RedisContainer redis = new RedisContainer("redis:6.2.6");

    protected String readJson(String jsonPath) {
        try (InputStream inputStream = new ClassPathResource(jsonPath).getInputStream();) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find file: " + jsonPath, e);
        }
    }

    protected String adminToken() {
        return jwtToken(Role.ADMIN);
    }

    protected String jwtToken(Role role) {
        return "Bearer " + jwtService.generateToken(
                JwtUserDetails.builder()
                        .id(1L)
                        .email("user@test.com")
                        .name("User")
                        .role(role)
                        .build()
        );
    }
}
