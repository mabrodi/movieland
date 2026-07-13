package org.dimchik.service.impl;

import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.dimchik.repository.PosterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosterServiceImplTest {

    @Mock
    private PosterRepository posterRepository;

    @InjectMocks
    private PosterServiceImpl posterService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
    }

    @Test
    void upsertPosterShouldCreateNewPosterWhenMovieHasNone() {
        posterService.upsertPoster(movie, "new_poster.jpg");

        ArgumentCaptor<Poster> captor = ArgumentCaptor.forClass(Poster.class);
        verify(posterRepository).save(captor.capture());

        Poster saved = captor.getValue();
        assertThat(saved.getPicturePath()).isEqualTo("new_poster.jpg");
        assertThat(saved.getMovie()).isSameAs(movie);
    }

    @Test
    void upsertPosterShouldUpdateExistingPoster() {
        Poster existingPoster = new Poster();
        existingPoster.setId(5L);
        existingPoster.setPicturePath("old_poster.jpg");
        existingPoster.setMovie(movie);
        movie.setPoster(existingPoster);

        posterService.upsertPoster(movie, "updated_poster.jpg");

        ArgumentCaptor<Poster> captor = ArgumentCaptor.forClass(Poster.class);
        verify(posterRepository).save(captor.capture());

        Poster saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(5L);
        assertThat(saved.getPicturePath()).isEqualTo("updated_poster.jpg");
        assertThat(saved.getMovie()).isSameAs(movie);
    }
}
