package org.dimchik.service.cache;

import org.dimchik.entity.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovieCacheServiceTest {

    private MovieCacheService movieCacheService;

    private Movie movie1;
    private Movie movie2;

    @BeforeEach
    void setUp() {
        movieCacheService = new MovieCacheService();

        movie1 = new Movie();
        movie1.setId(1L);
        movie1.setNameRussian("Побег из Шоушенка");

        movie2 = new Movie();
        movie2.setId(2L);
        movie2.setNameRussian("Крестный отец");
    }

    @Test
    void addShouldStoreMovieInCache() {
        movieCacheService.add(movie1);

        assertThat(movieCacheService.getById(1L)).isSameAs(movie1);
    }

    @Test
    void addShouldOverwriteExistingMovie() {
        Movie updated = new Movie();
        updated.setId(1L);
        updated.setNameRussian("Побег из Шоушенка (обновлённый)");

        movieCacheService.add(movie1);
        movieCacheService.add(updated);

        assertThat(movieCacheService.getById(1L)).isSameAs(updated);
        assertThat(movieCacheService.getById(1L).getNameRussian()).isEqualTo("Побег из Шоушенка (обновлённый)");
    }

    @Test
    void getByIdShouldReturnNullForMissingId() {
        assertThat(movieCacheService.getById(999L)).isNull();
    }

    @Test
    void getByIdShouldReturnCorrectMovieForEachId() {
        movieCacheService.add(movie1);
        movieCacheService.add(movie2);

        assertThat(movieCacheService.getById(1L)).isSameAs(movie1);
        assertThat(movieCacheService.getById(2L)).isSameAs(movie2);
    }

    @Test
    void invalidateShouldRemoveMovieFromCache() {
        movieCacheService.add(movie1);
        movieCacheService.invalidate(1L);

        assertThat(movieCacheService.getById(1L)).isNull();
    }

    @Test
    void invalidateShouldNotAffectOtherMovies() {
        movieCacheService.add(movie1);
        movieCacheService.add(movie2);

        movieCacheService.invalidate(1L);

        assertThat(movieCacheService.getById(1L)).isNull();
        assertThat(movieCacheService.getById(2L)).isSameAs(movie2);
    }

    @Test
    void invalidateNonExistentMovieShouldNotThrow() {
        assertThat(movieCacheService.getById(999L)).isNull();
        movieCacheService.invalidate(999L);
        assertThat(movieCacheService.getById(999L)).isNull();
    }
}
