package org.dimchik.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.dto.response.MovieResponse;
import org.dimchik.service.CurrencyService;
import org.dimchik.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "security.jwt.secret=0123456789abcdef0123456789abcdef",
        "security.jwt.ttl-seconds=900",
        "security.jwt.blacklist.cleanup-cron=0 * * * * *",
        "concurrent.enrichment.timeout=5"
})
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private CurrencyService currencyService;

    private List<MovieResponse> movieList;
    private MovieDetailResponse movieDetailResponse;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        movieList = objectMapper.readValue(
                new ClassPathResource("movieList.json").getFile(),
                new TypeReference<>() {}
        );
        movieDetailResponse = objectMapper.readValue(
                new ClassPathResource("movieFull.json").getFile(),
                MovieDetailResponse.class
        );
    }

    @Test
    void findAllShouldReturnMovieList() throws Exception {
        when(movieService.findAll(any(), any())).thenReturn(movieList);

        mockMvc.perform(get("/api/v1/movies").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(movieList.size()));

        verify(movieService).findAll(any(), any());
    }

    @Test
    void findByIdShouldReturnMovieDetail() throws Exception {
        when(movieService.findById(eq(1L))).thenReturn(movieDetailResponse);

        mockMvc.perform(get("/api/v1/movies/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nameRussian").value("Побег из Шоушенка"));

        verify(movieService).findById(eq(1L));
    }

    @Test
    void randomShouldReturnMovieList() throws Exception {
        List<MovieResponse> randomMovies = movieList.subList(0, 3);
        when(movieService.random(3)).thenReturn(randomMovies);

        mockMvc.perform(get("/api/v1/movies/random").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        verify(movieService).random(3);
    }

    @Test
    void findByGenreIdShouldReturnMovieList() throws Exception {
        when(movieService.findByGenreId(2L)).thenReturn(movieList);

        mockMvc.perform(get("/api/v1/movies/genre/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(movieService).findByGenreId(2L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void createShouldReturnMovieDetailWhenAdmin() throws Exception {
        when(movieService.create(any())).thenReturn(movieDetailResponse);

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildCreateRequestJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nameRussian").value("Побег из Шоушенка"));

        verify(movieService).create(any());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void createShouldReturn403WhenNotAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildCreateRequestJson()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(movieService);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateShouldReturnMovieDetailWhenAdmin() throws Exception {
        when(movieService.update(eq(1L), any())).thenReturn(movieDetailResponse);

        mockMvc.perform(put("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildUpdateRequestJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(movieService).update(eq(1L), any());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void updateShouldReturn403WhenNotAdmin() throws Exception {
        mockMvc.perform(put("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildUpdateRequestJson()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(movieService);
    }

    private String buildCreateRequestJson() {
        return """
                {
                    "nameRussian": "Побег из Шоушенка",
                    "nameNative": "The Shawshank Redemption",
                    "yearOfRelease": 1994,
                    "description": "Описание фильма",
                    "price": 123.45,
                    "rating": 8.9,
                    "picturePath": "poster.jpg",
                    "countries": [1],
                    "genres": [1]
                }
                """;
    }

    private String buildUpdateRequestJson() {
        return """
                {
                    "nameRussian": "Побег из Шоушенка (обновлённый)",
                    "nameNative": "The Shawshank Redemption",
                    "picturePath": "poster_new.jpg",
                    "countries": [1],
                    "genres": [1]
                }
                """;
    }
}
