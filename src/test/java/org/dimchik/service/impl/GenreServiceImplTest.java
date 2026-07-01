package org.dimchik.service.impl;

import org.dimchik.dto.GenreDTO;
import org.dimchik.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {
    private GenreServiceImpl genreService;

    @Mock
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        genreService = new GenreServiceImpl(genreRepository);
    }

    @Test
    void findAllShouldReturnEmptyList() {
        List<GenreDTO> result = genreService.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void findAllShouldReturnListGenres() {
        GenreDTO genre1 = new GenreDTO(1L, "Action");
        GenreDTO genre2 = new GenreDTO(2L, "Comedy");
        List<GenreDTO> genres = Arrays.asList(genre1, genre2);

        when(genreRepository.findAllCached()).thenReturn(genres);

        List<GenreDTO> result = genreService.findAll();

        assertThat(result).hasSize(2).containsExactly(genre1, genre2);
    }
}