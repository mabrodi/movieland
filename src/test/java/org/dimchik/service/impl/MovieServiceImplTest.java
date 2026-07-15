package org.dimchik.service.impl;

import org.dimchik.entity.Genre;
import org.dimchik.entity.Country;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.dimchik.repository.MovieRepository;
import org.dimchik.service.ConcurrentEnrichmentMovieService;
import org.dimchik.service.CountryService;
import org.dimchik.service.GenreService;
import org.dimchik.service.PosterService;
import org.dimchik.service.cache.MovieCacheService;
import org.dimchik.mapper.MovieMapper;
import org.dimchik.exception.MovieNotFoundException;
import org.dimchik.enums.SortDirection;
import org.dimchik.dto.request.CreateMovieRequest;
import org.dimchik.dto.request.UpdateMovieRequest;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.dto.response.MovieResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieMapper movieMapper;
    @Mock
    private MovieCacheService movieCacheService;
    @Mock
    private GenreService genreService;
    @Mock
    PosterService posterService;
    @Mock
    CountryService countryService;
    @Mock
    private ConcurrentEnrichmentMovieService concurrentEnrichmentMovieService;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie1;
    private Movie movie2;
    private MovieResponse response1;
    private MovieResponse response2;
    private MovieDetailResponse detailResponse;

    @BeforeEach
    void setUp() {
        Poster poster = new Poster();
        poster.setPicturePath("shawshank.jpg");

        movie1 = Movie.builder()
                .id(1L)
                .nameRussian("Побег из Шоушенка")
                .nameNative("The Shawshank Redemption")
                .yearOfRelease(1994)
                .rating(9.2)
                .price(120.4)
                .poster(poster)
                .build();

        movie2 = Movie.builder()
                .id(2L)
                .nameRussian("Крестный отец")
                .nameNative("The Godfather")
                .yearOfRelease(1972)
                .rating(9.1)
                .price(150.4)
                .poster(poster)
                .build();

        response1 = MovieResponse.builder()
                .id(1L)
                .nameRussian("Побег из Шоушенка")
                .nameNative("The Shawshank Redemption")
                .yearOfRelease(1994)
                .rating(9.2)
                .price(120.4)
                .picturePath("shawshank.jpg")
                .build();

        response2 = MovieResponse.builder()
                .id(2L)
                .nameRussian("Крестный отец")
                .nameNative("The Godfather")
                .yearOfRelease(1972)
                .rating(9.1)
                .price(150.4)
                .picturePath("shawshank.jpg")
                .build();

        detailResponse = MovieDetailResponse.builder()
                .id(1L)
                .nameRussian("Побег из Шоушенка")
                .nameNative("The Shawshank Redemption")
                .yearOfRelease(1994)
                .rating(9.2)
                .price(120.4)
                .description("Classic movie")
                .picturePath("shawshank.jpg")
                .countries(Collections.emptyList())
                .genres(Collections.emptyList())
                .reviews(Collections.emptyList())
                .build();
    }

    @Test
    void findAllShouldUseRepositorySortAndReturnMappedResponses() {
        when(movieRepository.findAll(any(Sort.class))).thenReturn(List.of(movie1, movie2));
        when(movieMapper.toResponseList(List.of(movie1, movie2))).thenReturn(List.of(response1, response2));

        List<MovieResponse> result = movieService.findAll(SortDirection.DESC, null);

        assertThat(result).containsExactly(response1, response2);
        verify(movieRepository).findAll(any(Sort.class));
        verify(movieMapper).toResponseList(List.of(movie1, movie2));
    }

    @Test
    void findAllShouldReturnEmptyWhenRepoReturnsEmpty() {
        when(movieRepository.findAll(any(Sort.class))).thenReturn(List.of());
        when(movieMapper.toResponseList(List.of())).thenReturn(List.of());

        List<MovieResponse> result = movieService.findAll(SortDirection.DESC, null);

        assertThat(result).isEmpty();
        verify(movieRepository).findAll(any(Sort.class));
    }

    @Test
    void randomShouldReturnMappedResponses() {
        when(movieRepository.findRandomMovies(PageRequest.of(0, 3))).thenReturn(List.of(movie1));
        when(movieMapper.toResponseList(List.of(movie1))).thenReturn(List.of(response1));

        List<MovieResponse> result = movieService.random(3);

        assertThat(result).containsExactly(response1);
        verify(movieRepository).findRandomMovies(PageRequest.of(0, 3));
    }

    @Test
    void randomShouldReturnEmptyWhenNoMovies() {
        when(movieRepository.findRandomMovies(PageRequest.of(0, 5))).thenReturn(List.of());
        when(movieMapper.toResponseList(List.of())).thenReturn(List.of());

        List<MovieResponse> result = movieService.random(5);

        assertThat(result).isEmpty();
    }

    @Test
    void findByGenreIdShouldReturnMappedResponses() {
        when(movieRepository.findMoviesByGenreId(2L)).thenReturn(List.of(movie1));
        when(movieMapper.toResponseList(List.of(movie1))).thenReturn(List.of(response1));

        List<MovieResponse> result = movieService.findByGenreId(2L);

        assertThat(result).containsExactly(response1);
        verify(movieRepository).findMoviesByGenreId(2L);
    }

    @Test
    void findByIdShouldReturnCachedMovieWhenAvailable() {
        when(movieCacheService.getById(1L)).thenReturn(movie1);
        when(movieMapper.toDetailResponse(movie1)).thenReturn(detailResponse);

        MovieDetailResponse result = movieService.findById(1L);

        assertThat(result).isSameAs(detailResponse);
        verify(movieCacheService).getById(1L);
        verifyNoInteractions(movieRepository, concurrentEnrichmentMovieService);
        verify(movieMapper).toDetailResponse(movie1);
    }

    @Test
    void findByIdShouldLoadFromDbWhenNotInCache() {
        when(movieCacheService.getById(1L)).thenReturn(null);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieMapper.toDetailResponse(movie1)).thenReturn(detailResponse);

        MovieDetailResponse result = movieService.findById(1L);

        assertThat(result).isSameAs(detailResponse);
        verify(movieCacheService).getById(1L);
        verify(movieRepository).findById(1L);
        verify(concurrentEnrichmentMovieService).enrichMovie(movie1);
        verify(movieCacheService).add(movie1);
    }

    @Test
    void findByIdShouldThrowWhenMovieNotFoundInDb() {
        when(movieCacheService.getById(999L)).thenReturn(null);
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.findById(999L))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("Movie not found with id: 999");

        verify(movieCacheService).getById(999L);
        verify(movieRepository).findById(999L);
        verify(concurrentEnrichmentMovieService, never()).enrichMovie(any());
        verify(movieCacheService, never()).add(any());
    }

    @Test
    void createShouldMapAssembleSaveAndReturnDetailResponse() {
        CreateMovieRequest request = new CreateMovieRequest();
        request.setPicturePath("poster.jpg");
        request.setGenres(List.of(2L));
        request.setCountries(List.of(1L));
        request.setNameRussian("Новый фильм");
        request.setNameNative("New Movie");
        request.setYearOfRelease(2024);
        request.setDescription("Описание");
        request.setPrice(100.0);
        request.setRating(8.5);

        Movie mapped = new Movie();
        mapped.setId(5L);

        Genre genre = new Genre();
        genre.setId(2L);
        Country country = new Country();
        country.setId(1L);

        when(movieMapper.createMovieFromEntity(request)).thenReturn(mapped);
        when(genreService.findAllIds(List.of(2L))).thenReturn(List.of(genre));
        when(countryService.findAllIds(List.of(1L))).thenReturn(List.of(country));
        when(movieRepository.save(mapped)).thenReturn(mapped);
        when(movieMapper.toDetailResponse(mapped)).thenReturn(detailResponse);

        MovieDetailResponse result = movieService.create(request);

        assertThat(result).isSameAs(detailResponse);

        verify(movieMapper).createMovieFromEntity(request);
        verify(genreService).findAllIds(List.of(2L));
        verify(countryService).findAllIds(List.of(1L));
        verify(posterService).upsertPoster(mapped, "poster.jpg");
        verify(movieRepository).save(mapped);
        verify(movieCacheService).invalidate(5L);

        assertThat(mapped.getGenres()).containsExactly(genre);
        assertThat(mapped.getCountries()).containsExactly(country);
    }

    @Test
    void updateShouldLoadApplyAndSave() {
        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setPicturePath("new.jpg");
        request.setGenres(List.of(4L));
        request.setCountries(List.of(3L));

        Movie existing = new Movie();
        existing.setId(1L);

        Genre genre = new Genre();
        genre.setId(4L);
        Country country = new Country();
        country.setId(3L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(genreService.findAllIds(List.of(4L))).thenReturn(List.of(genre));
        when(countryService.findAllIds(List.of(3L))).thenReturn(List.of(country));
        when(movieRepository.save(existing)).thenReturn(existing);
        when(movieMapper.toDetailResponse(existing)).thenReturn(detailResponse);

        MovieDetailResponse result = movieService.update(1L, request);

        assertThat(result).isSameAs(detailResponse);

        verify(movieMapper).updateMovieFromRequest(request, existing);
        verify(genreService).findAllIds(List.of(4L));
        verify(countryService).findAllIds(List.of(3L));
        verify(posterService).upsertPoster(existing, "new.jpg");
        verify(movieRepository).save(existing);
        verify(movieCacheService).invalidate(1L);

        assertThat(existing.getGenres()).containsExactly(genre);
        assertThat(existing.getCountries()).containsExactly(country);
    }

    @Test
    void updateShouldThrowWhenMovieNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.update(1L, new UpdateMovieRequest()))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("Movie not found with id: 1");

        verify(movieRepository).findById(1L);
        verifyNoMoreInteractions(movieRepository);
    }
}
