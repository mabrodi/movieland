package org.dimchik.service.cache;

import org.dimchik.entity.Movie;
import org.dimchik.repository.MovieRatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieRatingCacheServiceTest {

    @Mock
    private MovieRatingRepository movieRatingRepository;

    @InjectMocks
    private MovieRatingCacheService movieRatingCacheService;

    @BeforeEach
    void setUp() {
        lenient().when(movieRatingRepository.findAll()).thenReturn(List.of());
    }

    @Test
    void enrichMovieWithRatingShouldSetRatingWhenCached() {
        movieRatingCacheService.createUserMovieRating(1L, 8.5, 1L);
        movieRatingCacheService.createUserMovieRating(1L, 9.5, 2L);

        Movie movie = new Movie();
        movie.setId(1L);

        movieRatingCacheService.enrichMovieWithRating(movie);

        assertThat(movie.getRating()).isEqualTo(9.0);
    }

    @Test
    void enrichMovieWithRatingShouldNotChangeRatingWhenNotCached() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setRating(5.0);

        movieRatingCacheService.enrichMovieWithRating(movie);

        assertThat(movie.getRating()).isEqualTo(5.0);
    }

    @Test
    void enrichMovieWithRatingShouldCalculateAverageCorrectlyForThreeRatings() {
        movieRatingCacheService.createUserMovieRating(1L, 6.0, 1L);
        movieRatingCacheService.createUserMovieRating(1L, 8.0, 2L);
        movieRatingCacheService.createUserMovieRating(1L, 10.0, 3L);

        Movie movie = new Movie();
        movie.setId(1L);

        movieRatingCacheService.enrichMovieWithRating(movie);

        assertThat(movie.getRating()).isCloseTo(8.0, org.assertj.core.api.Assertions.within(0.01));
    }

    @Test
    void removeByMovieIdShouldClearMovieRatingCache() {
        movieRatingCacheService.createUserMovieRating(1L, 8.5, 1L);
        movieRatingCacheService.createUserMovieRating(2L, 9.0, 1L);

        movieRatingCacheService.removeByMovieId(1L);

        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setRating(5.0);
        movieRatingCacheService.enrichMovieWithRating(movie1);
        assertThat(movie1.getRating()).isEqualTo(5.0);

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movieRatingCacheService.enrichMovieWithRating(movie2);
        assertThat(movie2.getRating()).isEqualTo(9.0);
    }

    @Test
    void removeByMovieIdShouldNotAffectOtherMovies() {
        movieRatingCacheService.createUserMovieRating(1L, 8.5, 1L);
        movieRatingCacheService.createUserMovieRating(2L, 9.0, 1L);

        movieRatingCacheService.removeByMovieId(1L);

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movieRatingCacheService.enrichMovieWithRating(movie2);

        assertThat(movie2.getRating()).isEqualTo(9.0);
    }

    @Test
    void createUserMovieRatingShouldQueuePendingRating() {
        movieRatingCacheService.createUserMovieRating(1L, 8.5, 1L);

        verify(movieRatingRepository, never()).saveAll(any());
    }

    @Test
    void removeByMovieIdShouldHandleEmptyCache() {
        movieRatingCacheService.removeByMovieId(999L);

        Movie movie = new Movie();
        movie.setId(999L);
        movie.setRating(5.0);
        movieRatingCacheService.enrichMovieWithRating(movie);
        assertThat(movie.getRating()).isEqualTo(5.0);
    }
}
