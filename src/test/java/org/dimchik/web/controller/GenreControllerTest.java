package org.dimchik.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dimchik.dto.GenreDTO;
import org.dimchik.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
class GenreControllerTest {
    private static final String ROUTES_API_URL = "/api/v1/genre";

    private MockMvc mockMvc;

    @Mock
    private GenreService genreService;

    private List<GenreDTO> expectedGenres;
    private String expectedJson;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new GenreController(genreService)).build();
        ObjectMapper objectMapper = new ObjectMapper();

        File jsonFile = new ClassPathResource("genreList.json").getFile();
        expectedJson = Files.readString(jsonFile.toPath());
        expectedGenres = objectMapper.readValue(expectedJson, new TypeReference<>() {});
    }

    @Test
    void findAllReturnCorrectJson() throws Exception {
        when(genreService.findAll()).thenReturn(expectedGenres);

        mockMvc.perform(get(ROUTES_API_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}