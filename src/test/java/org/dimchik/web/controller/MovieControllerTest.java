package org.dimchik.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dimchik.common.request.CreateMovieRequest;
import org.dimchik.common.request.MovieByIdRequest;
import org.dimchik.common.request.MovieRequest;
import org.dimchik.common.request.UpdateMovieRequest;
import org.dimchik.dto.MovieDTO;
import org.dimchik.dto.MovieFullDTO;
import org.dimchik.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class MovieControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MovieService movieService;

    private List<MovieDTO> movieList;
    private MovieFullDTO movieFullDTO;
    private String movieListJson;
    private String movieFullJson;
    private String movieCreateJson;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new MovieController(movieService)).build();
        ObjectMapper objectMapper = new ObjectMapper();

        File listFile = new ClassPathResource("movieList.json").getFile();
        File fullFile = new ClassPathResource("movieFull.json").getFile();
        File createFile = new ClassPathResource("movieCreate.json").getFile();

        movieListJson = Files.readString(listFile.toPath());
        movieFullJson = Files.readString(fullFile.toPath());
        movieCreateJson = Files.readString(createFile.toPath());

        movieList = objectMapper.readValue(movieListJson, new TypeReference<>() {});
        movieFullDTO = objectMapper.readValue(movieFullJson, MovieFullDTO.class);
    }

    @Test
    void findAllReturnCorrectJson() throws Exception {
        when(movieService.findAll(any(MovieRequest.class))).thenReturn(movieList);

        mockMvc.perform(get("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieListJson));
    }

    @Test
    void findByIdReturnCorrectJson() throws Exception {
        when(movieService.findById(eq(1L), any(MovieByIdRequest.class))).thenReturn(movieFullDTO);

        mockMvc.perform(get("/api/v1/movie/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieFullJson));
    }

    @Test
    void randomReturnCorrectJson() throws Exception {
        when(movieService.random(3)).thenReturn(movieList);

        mockMvc.perform(get("/api/v1/movies/random")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieListJson));
    }

    @Test
    void findByGenreIdReturnCorrectJson() throws Exception {
        when(movieService.findByGenreId(2L)).thenReturn(movieList);

        mockMvc.perform(get("/api/v1/movie/genre/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(movieListJson));
    }

    @Test
    void createShouldReturnMovieFullJson() throws Exception {
        when(movieService.create(any(CreateMovieRequest.class))).thenReturn(movieFullDTO);

        mockMvc.perform(post("/api/v1/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieCreateJson))
                .andExpect(status().isOk())
                .andExpect(content().json(movieFullJson));
    }

    @Test
    void updateShouldReturnUpdatedMovieJson() throws Exception {
        when(movieService.update(eq(1L), any(UpdateMovieRequest.class))).thenReturn(movieFullDTO);

        mockMvc.perform(put("/api/v1/movie/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieCreateJson))
                .andExpect(status().isOk())
                .andExpect(content().json(movieFullJson));
    }
}