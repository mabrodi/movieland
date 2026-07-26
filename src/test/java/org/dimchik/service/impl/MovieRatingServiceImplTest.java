package org.dimchik.service.impl;

import org.dimchik.dto.JwtUserDetails;
import org.dimchik.dto.response.MovieRatingResponse;
import org.dimchik.entity.Movie;
import org.dimchik.enums.Role;
import org.dimchik.exception.AlreadyRatedException;
import org.dimchik.service.cache.MovieRatingCacheService;
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
class MovieRatingServiceImplTest {

    @Mock
    private MovieRatingCacheService movieRatingCacheService;

    @InjectMocks
    private MovieRatingServiceImpl movieRatingService;

    private JwtUserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = JwtUserDetails.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void findByMovieIdAndUserShouldReturnRatingWhenExists() {
        when(movieRatingCacheService.existsUserRating(10L, 1L)).thenReturn(true);
        when(movieRatingCacheService.getUserMovieRating(10L, 1L)).thenReturn(8.5);

        MovieRatingResponse result = movieRatingService.findByMovieIdAndUser(10L, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getMovieId()).isEqualTo(10L);
        assertThat(result.getRating()).isEqualTo(8.5);
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(movieRatingCacheService).existsUserRating(10L, 1L);
        verify(movieRatingCacheService).getUserMovieRating(10L, 1L);
    }

    @Test
    void findByMovieIdAndUserShouldThrowWhenNotRated() {
        when(movieRatingCacheService.existsUserRating(10L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> movieRatingService.findByMovieIdAndUser(10L, userDetails))
                .isInstanceOf(AlreadyRatedException.class)
                .hasMessageContaining("This movie no rating");

        verify(movieRatingCacheService).existsUserRating(10L, 1L);
        verify(movieRatingCacheService, never()).getUserMovieRating(anyLong(), anyLong());
    }

    @Test
    void createShouldSaveRatingAndReturnResponse() {
        when(movieRatingCacheService.existsUserRating(10L, 1L)).thenReturn(false);

        MovieRatingResponse result = movieRatingService.create(10L, 8.5, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getMovieId()).isEqualTo(10L);
        assertThat(result.getRating()).isEqualTo(8.5);
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(movieRatingCacheService).existsUserRating(10L, 1L);
        verify(movieRatingCacheService).createUserMovieRating(10L, 8.5, 1L);
    }

    @Test
    void createShouldThrowWhenAlreadyRated() {
        when(movieRatingCacheService.existsUserRating(10L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> movieRatingService.create(10L, 8.5, userDetails))
                .isInstanceOf(AlreadyRatedException.class)
                .hasMessageContaining("This movie has already been rated");

        verify(movieRatingCacheService).existsUserRating(10L, 1L);
        verify(movieRatingCacheService, never()).createUserMovieRating(anyLong(), anyDouble(), anyLong());
    }

    @Test
    void updateShouldUpdateRatingAndReturnResponse() {
        when(movieRatingCacheService.existsUserRating(10L, 1L)).thenReturn(true);

        MovieRatingResponse result = movieRatingService.update(10L, 9.0, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.getMovieId()).isEqualTo(10L);
        assertThat(result.getRating()).isEqualTo(9.0);
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(movieRatingCacheService).existsUserRating(10L, 1L);
        verify(movieRatingCacheService).updateUserMovieRating(10L, 9.0, 1L);
    }

    @Test
    void updateShouldThrowWhenNotRated() {
        when(movieRatingCacheService.existsUserRating(10L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> movieRatingService.update(10L, 9.0, userDetails))
                .isInstanceOf(AlreadyRatedException.class)
                .hasMessageContaining("This movie no rating. Should create rating");

        verify(movieRatingCacheService).existsUserRating(10L, 1L);
        verify(movieRatingCacheService, never()).updateUserMovieRating(anyLong(), anyDouble(), anyLong());
    }

    @Test
    void enrichSingleMovieShouldDelegateToCacheService() {
        Movie movie = new Movie();
        movie.setId(1L);

        movieRatingService.enrichSingleMovieByRating(movie);

        verify(movieRatingCacheService).enrichMovieWithRating(movie);
    }

    @Test
    void enrichMultipleMoviesShouldDelegateToCacheServiceForEach() {
        Movie movie1 = new Movie();
        movie1.setId(1L);
        Movie movie2 = new Movie();
        movie2.setId(2L);

        movieRatingService.enrichSingleMovieByRating(List.of(movie1, movie2));

        verify(movieRatingCacheService).enrichMovieWithRating(movie1);
        verify(movieRatingCacheService).enrichMovieWithRating(movie2);
    }

    @Test
    void removeRatingByMovieIdShouldDelegateToCacheServiceForEachId() {
        movieRatingService.removeRatingByMovieId(List.of(1L, 2L, 3L));

        verify(movieRatingCacheService).removeByMovieId(1L);
        verify(movieRatingCacheService).removeByMovieId(2L);
        verify(movieRatingCacheService).removeByMovieId(3L);
    }

    @Test
    void removeRatingByMovieIdShouldDoNothingForEmptyList() {
        movieRatingService.removeRatingByMovieId(List.of());

        verifyNoInteractions(movieRatingCacheService);
    }
}
