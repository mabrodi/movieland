package org.dimchik.web.controller;

import org.assertj.core.util.Maps;
import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {
    private MovieController movieController;
    private MovieResponseDTO movieResponseDTO;

    @Mock
    MovieService movieService;

    @BeforeEach
    void setUp() {
        movieResponseDTO = new MovieResponseDTO(
                1L,
                "Побег из Шоушенка",
                "The Shawshank Redemption",
                1994,
                8.90,
                123.12,
                "http://link"
        );

        movieController = new MovieController(movieService);
    }

    @Test
    void findAllShouldReturnListMovies() {
        List<MovieResponseDTO> list = List.of(movieResponseDTO);
        when(movieService.findAll(null, null)).thenReturn(list);
        ResponseEntity<List<MovieResponseDTO>> response = movieController.findAll(null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
        verify(movieService).findAll(null, null);
    }

    @Test
    void findByIdShouldReturnMovie() {
        when(movieService.findById(1)).thenReturn(movieResponseDTO);
        ResponseEntity<MovieResponseDTO> response = movieController.findById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(movieResponseDTO);
        verify(movieService).findById(1);
    }

    @Test
    void randomShouldReturnListMovies() {
        List<MovieResponseDTO> list = List.of(movieResponseDTO);
        when(movieService.random(3)).thenReturn(list);
        ResponseEntity<List<MovieResponseDTO>> response = movieController.random();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
        verify(movieService).random(3);
    }

    @Test
    void findByGenreIdShouldReturnListMovies() {
        List<MovieResponseDTO> list = List.of(movieResponseDTO);
        when(movieService.findByGenreId(1)).thenReturn(list);
        ResponseEntity<List<MovieResponseDTO>> response = movieController.findByGenreId(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
        verify(movieService).findByGenreId(1);
    }
}