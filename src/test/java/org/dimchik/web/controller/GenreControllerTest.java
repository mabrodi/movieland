package org.dimchik.web.controller;

import org.dimchik.dto.GenreResponseDTO;
import org.dimchik.service.GenreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {
    @Mock
    GenreService genreService;

    @Test
    void findAllShouldReturnListOfGenres() {
        GenreResponseDTO genreResponseDTO = new GenreResponseDTO(1L, "Test");
        List<GenreResponseDTO> list = List.of(genreResponseDTO);
        when(genreService.findAll()).thenReturn(list);

        GenreController genreController = new GenreController(genreService);
        ResponseEntity<List<GenreResponseDTO>> response = genreController.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
        verify(genreService).findAll();
    }
}