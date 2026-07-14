package org.dimchik.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dimchik.dto.response.GenreResponse;
import org.dimchik.security.AuthFilter;
import org.dimchik.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
@AutoConfigureMockMvc(addFilters = false)
class GenreControllerTest extends AbstractBaseTest {
    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private AuthFilter authFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String genreListJson;
    private List<GenreResponse> genreList;

    @BeforeEach
    void setUp() throws Exception {
        genreListJson = readJson("genreList.json");
        genreList = objectMapper.readValue(genreListJson, new TypeReference<List<GenreResponse>>() {});
    }

    @Test
    void findAllShouldReturnGenreList() throws Exception {
        when(genreService.findAll()).thenReturn(genreList);

        mockMvc.perform(get("/api/v1/genres").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(genreListJson));
    }

    @Test
    void findAllShouldReturnEmptyList() throws Exception {
        when(genreService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/genres").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
