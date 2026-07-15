package org.dimchik.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.dto.response.MovieResponse;
import org.dimchik.security.AuthFilter;
import org.dimchik.service.CurrencyService;
import org.dimchik.service.MovieService;
import org.dimchik.mapper.MovieMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest extends AbstractBaseTest {
    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private CurrencyService currencyService;

    @MockitoBean
    private AuthFilter authFilter;

    @MockitoBean
    private MovieMapper movieMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String movieListJson;
    private String movieFullJson;
    private String movieCreateJson;
    private String movieUpdateJson;
    private List<MovieResponse> movieList;
    private MovieDetailResponse movieDetailResponse;

    @BeforeEach
    void setUp() throws Exception {
        movieListJson = readJson("movieListResponse.json");
        movieFullJson = readJson("movieFullResponse.json");
        movieCreateJson = readJson("movieCreateRequest.json");
        movieUpdateJson = readJson("movieUpdateRequest.json");

        movieList = objectMapper.readValue(movieListJson, new TypeReference<List<MovieResponse>>() {});
        movieDetailResponse = objectMapper.readValue(movieFullJson, MovieDetailResponse.class);
    }

    @Test
    @WithMockUser
    void findAllShouldReturnMovieList() throws Exception {
        when(movieService.findAll(any(), any())).thenReturn(movieList);

        mockMvc.perform(get("/api/v1/movies").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieListJson));

        verify(movieService).findAll(any(), any());
    }

    @Test
    void findByIdShouldReturnMovieDetail() throws Exception {
        when(movieService.findById(eq(1L))).thenReturn(movieDetailResponse);

        mockMvc.perform(get("/api/v1/movies/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieFullJson));

        verify(movieService).findById(eq(1L));
    }

    @Test
    void randomShouldReturnMovieList() throws Exception {
        when(movieService.random(3)).thenReturn(movieList);

        mockMvc.perform(get("/api/v1/movies/random").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieListJson));

        verify(movieService).random(3);
    }

    @Test
    void findByGenreIdShouldReturnMovieList() throws Exception {
        when(movieService.findByGenreId(2L)).thenReturn(movieList);

        mockMvc.perform(get("/api/v1/movies/genre/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieListJson));

        verify(movieService).findByGenreId(2L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createShouldReturnMovieDetailWhenAdmin() throws Exception {
        when(movieService.create(any())).thenReturn(movieDetailResponse);

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieCreateJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieFullJson));

        verify(movieService).create(any());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateShouldReturnMovieDetailWhenAdmin() throws Exception {
        when(movieService.update(eq(1L), any())).thenReturn(movieDetailResponse);

        mockMvc.perform(put("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieUpdateJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieFullJson));

        verify(movieService).update(eq(1L), any());
    }
}
