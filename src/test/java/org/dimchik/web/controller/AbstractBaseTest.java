package org.dimchik.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AbstractBaseTest {
    @Autowired
    protected MockMvc mockMvc;


    protected String readJson(String jsonPath) {
        try (InputStream inputStream = new ClassPathResource(jsonPath).getInputStream();) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to find file: " + jsonPath, e);
        }
    }
}
