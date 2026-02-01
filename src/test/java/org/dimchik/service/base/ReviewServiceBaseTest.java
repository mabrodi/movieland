package org.dimchik.service.base;

import org.dimchik.dto.ReviewDTO;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Review;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.ReviewRepository;
import org.dimchik.repository.UserRepository;
import org.dimchik.web.request.CreateReviewRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceBaseTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceBase reviewService;

    @Test
    void createShouldSaveReviewAndReturnDto() {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setMovieId(10L);
        request.setText("очень крутой фильм");

        Movie movie = new Movie();
        movie.setId(10L);

        org.dimchik.entity.User dbUser = new org.dimchik.entity.User();
        dbUser.setId(7L);
        dbUser.setName("Ronald");
        dbUser.setEmail("ronald@example.com");

        UserDetails principal = new User(
                "ronald@example.com",
                "ignored",
                List.of(new SimpleGrantedAuthority("USER"))
        );

        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("ronald@example.com")).thenReturn(Optional.of(dbUser));

        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(123L);
            return r;
        });

        ReviewDTO result = reviewService.create(request, principal);

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
        CreateReviewRequest request = new CreateReviewRequest();
        request.setMovieId(999L);
        request.setText("text");

        UserDetails principal = new User("u@example.com", "x", List.of());

        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(request, principal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Movie not found with id: 999");

        verify(movieRepository).findById(999L);
        verifyNoInteractions(userRepository, reviewRepository);
    }

    @Test
    void createShouldThrowWhenUserNotFound() {
        var request = new CreateReviewRequest();
        request.setMovieId(10L);
        request.setText("text");

        var movie = new Movie();
        movie.setId(10L);

        UserDetails principal = new User("missing@example.com", "x", List.of());

        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(request, principal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with id: missing@example.com");

        verify(movieRepository).findById(10L);
        verify(userRepository).findByEmail("missing@example.com");
        verifyNoInteractions(reviewRepository);
    }
}