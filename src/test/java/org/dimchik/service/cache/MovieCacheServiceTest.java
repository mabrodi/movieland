package org.dimchik.service.cache;

import org.dimchik.entity.Movie;
import org.dimchik.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieCacheServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieCacheService movieCacheService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setNameRussian("Побег из Шоушенка");
    }

    @Test
    void getMovieShouldLoadFromDbAndPutToCacheWhenNotCached() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Movie result1 = movieCacheService.getMovie(1L);
        Movie result2 = movieCacheService.getMovie(1L);

        assertThat(result1).isSameAs(movie);
        assertThat(result2).isSameAs(movie);

        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieShouldReturnNullWhenMovieNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        Movie result = movieCacheService.getMovie(99L);

        assertThat(result).isNull();
        verify(movieRepository).findById(99L);
    }

    @Test
    void getMovieShouldReturnCachedValueWithoutCallingRepository() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Movie first = movieCacheService.getMovie(1L);
        Movie second = movieCacheService.getMovie(1L);

        assertThat(first).isSameAs(movie);
        assertThat(second).isSameAs(movie);

        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void updateMovieShouldUpdateDbAndCacheWhenMovieIsCached() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        movieCacheService.getMovie(1L);

        Movie updated = new Movie();
        updated.setId(1L);
        updated.setNameRussian("Обновлённое название");

        movieCacheService.updateMovie(updated);

        verify(movieRepository).save(updated);

        Movie cachedAfterUpdate = movieCacheService.getMovie(1L);
        assertThat(cachedAfterUpdate).isSameAs(updated);
    }

    @Test
    void updateMovieShouldUpdateDbOnlyWhenMovieNotCached() {
        Movie updated = new Movie();
        updated.setId(2L);
        updated.setNameRussian("Новый фильм");

        movieCacheService.updateMovie(updated);

        verify(movieRepository).save(updated);

        when(movieRepository.findById(2L)).thenReturn(Optional.of(updated));

        Movie result = movieCacheService.getMovie(2L);
        assertThat(result).isSameAs(updated);

        verify(movieRepository).findById(2L);
    }
}