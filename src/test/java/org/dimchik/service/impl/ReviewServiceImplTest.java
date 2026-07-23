package org.dimchik.service.impl;

import org.dimchik.dto.JwtUserDetails;
import org.dimchik.dto.request.CreateReviewRequest;
import org.dimchik.dto.response.ReviewResponse;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Review;
import org.dimchik.entity.User;
import org.dimchik.enums.Role;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.ReviewRepository;
import org.dimchik.repository.UserRepository;
import org.dimchik.exception.MovieNotFoundException;
import org.dimchik.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Movie movie;
    private User dbUser;
    private CreateReviewRequest request;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(10L);
        movie.setNameRussian("Побег из Шоушенка");

        dbUser = new User();
        dbUser.setId(7L);
        dbUser.setName("Ronald");
        dbUser.setEmail("ronald@example.com");

        request = new CreateReviewRequest();
        request.setMovieId(10L);
        request.setText("очень крутой фильм");
    }

    @Test
    void createShouldSaveReviewAndReturnResponse() {
        JwtUserDetails principal = new JwtUserDetails(1, "user", "ronald@example.com", Role.USER);
        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("ronald@example.com")).thenReturn(Optional.of(dbUser));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(123L);
            return r;
        });

        ReviewResponse result = reviewService.create(request, principal);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(123L);
        assertThat(result.getComment()).isEqualTo("очень крутой фильм");
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getId()).isEqualTo(7L);
        assertThat(result.getUser().getName()).isEqualTo("Ronald");

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());

        Review saved = captor.getValue();
        assertThat(saved.getMovie()).isSameAs(movie);
        assertThat(saved.getAuthor()).isSameAs(dbUser);
        assertThat(saved.getComment()).isEqualTo("очень крутой фильм");

        verify(movieRepository).findById(10L);
        verify(userRepository).findByEmail("ronald@example.com");
        verifyNoMoreInteractions(movieRepository, userRepository, reviewRepository);
    }

    @Test
    void createShouldThrowWhenMovieNotFound() {
        JwtUserDetails principal = new JwtUserDetails(1, "user", "ronald@example.com", Role.USER);

        when(movieRepository.findById(999L)).thenReturn(Optional.empty());
        request.setMovieId(999L);

        assertThatThrownBy(() -> reviewService.create(request, principal))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("Movie not found with id: 999");

        verify(movieRepository).findById(999L);
        verifyNoInteractions(userRepository, reviewRepository);
    }

    @Test
    void createShouldThrowWhenUserNotFound() {
        JwtUserDetails principal = new JwtUserDetails(1, "user", "missing@example.com", Role.USER);

        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(request, principal))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found: missing@example.com");

        verify(movieRepository).findById(10L);
        verify(userRepository).findByEmail("missing@example.com");
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void enrichSingleMovieByReviewesShouldSetReviewsOnMovie() {
        Review review1 = new Review();
        review1.setId(1L);
        review1.setComment("Отличный фильм");
        Review review2 = new Review();
        review2.setId(2L);
        review2.setComment("Шедевр");

        when(reviewRepository.findAllByMovieId(10L)).thenReturn(List.of(review1, review2));

        reviewService.enrichSingleMovieByReviewes(movie);

        assertThat(movie.getReviews()).hasSize(2).containsExactly(review1, review2);
        verify(reviewRepository).findAllByMovieId(10L);
    }

    @Test
    void enrichSingleMovieByReviewesShouldNotSetWhenThreadInterrupted() {
        Thread.currentThread().interrupt();

        try {
            reviewService.enrichSingleMovieByReviewes(movie);
            assertThat(movie.getReviews()).isNull();
        } finally {
            Thread.interrupted();
        }

        verify(reviewRepository).findAllByMovieId(10L);
    }
}
