package org.dimchik.service.impl;

import org.dimchik.dto.response.GenreResponse;
import org.dimchik.entity.Country;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;
import org.dimchik.repository.GenreRepository;
import org.dimchik.service.mapper.GenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;
    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
    }

    @Test
    void findAllShouldReturnMappedResponses() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Action");
        Genre genre2 = new Genre();
        genre2.setId(2L);
        genre2.setName("Comedy");

        GenreResponse response1 = new GenreResponse();
        GenreResponse response2 = new GenreResponse();

        when(genreRepository.findAll()).thenReturn(List.of(genre1, genre2));
        when(genreMapper.toResponseList(List.of(genre1, genre2))).thenReturn(List.of(response1, response2));

        List<GenreResponse> result = genreService.findAll();

        assertThat(result).hasSize(2).containsExactly(response1, response2);
        verify(genreRepository).findAll();
    }

    @Test
    void findAllShouldReturnEmptyList() {
        when(genreRepository.findAll()).thenReturn(List.of());
        when(genreMapper.toResponseList(List.of())).thenReturn(List.of());

        List<GenreResponse> result = genreService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void enrichSingleMovieByGenresShouldSetGenresOnMovie() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Action");
        Genre genre2 = new Genre();
        genre2.setId(2L);
        genre2.setName("Comedy");

        when(genreRepository.findAllByMovieId(1L)).thenReturn(List.of(genre1, genre2));

        genreService.enrichSingleMovieByGenres(movie);

        assertThat(movie.getGenres()).hasSize(2).containsExactly(genre1, genre2);
        verify(genreRepository).findAllByMovieId(1L);
    }

    @Test
    void enrichSingleMovieByGenresShouldNotSetWhenThreadInterrupted() {
        Thread.currentThread().interrupt();

        try {
            genreService.enrichSingleMovieByGenres(movie);
            assertThat(movie.getGenres()).isNull();
        } finally {
            Thread.interrupted();
        }

        verify(genreRepository).findAllByMovieId(1L);
    }

    @Test
    void findAllIdsShouldGetGenres() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);

        when(genreRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(genre1, genre2));

        List<Genre> genres = genreService.findAllIds(List.of(1L, 2L));
        assertThat(genres).hasSize(2).containsExactly(genre1, genre2);
        verify(genreRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    void findAllIdsShouldDoNothingWhenIdsNull() {
        List<Genre> genres = genreService.findAllIds(null);
        assertThat(genres).isEmpty();
    }

    @Test
    void findAllIdsShouldDoNothingWhenIdsEmpty() {
        List<Genre> genres = genreService.findAllIds(List.of());
        assertThat(genres).isEmpty();
    }

    @Test
    void findAllIdsShouldThrowWhenGenresNotFound() {
        when(genreRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(new Genre()));

        assertThatThrownBy(() -> genreService.findAllIds(List.of(1L, 2L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Some genres not found");

        verify(genreRepository).findAllById(List.of(1L, 2L));
    }
}
