package org.dimchik.service.impl;

import org.dimchik.enums.Currency;
import org.dimchik.web.response.MovieDetailResponse;
import org.dimchik.web.response.MovieResponse;
import org.dimchik.dto.RateDTO;
import org.dimchik.entity.Country;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.dimchik.repository.CountryRepository;
import org.dimchik.repository.GenreRepository;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.PosterRepository;
import org.dimchik.repository.mapper.MovieMapper;
import org.dimchik.service.cache.MovieCacheService;
import org.dimchik.service.cache.RateCacheService;
import org.dimchik.service.enrichment.MovieEnrichmentService;
import org.dimchik.web.exception.ResourceNotFoundException;
import org.dimchik.enums.SortDirection;
import org.dimchik.web.request.CreateMovieRequest;
import org.dimchik.web.request.UpdateMovieRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

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
    private MovieEnrichmentService movieEnrichmentService;
    @Mock
    private MovieCacheService movieCacheService;
    @Mock
    private RateCacheService rateCacheService;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private PosterRepository posterRepository;
    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie1;
    private Movie movie2;
    private MovieResponse movieResponse1;
    private MovieResponse movieResponse2;
    private MovieDetailResponse fullDtoBase;

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

        movieResponse1 = MovieResponse.builder()
                .id(1L)
                .nameRussian("Побег из Шоушенка")
                .nameNative("The Shawshank Redemption")
                .yearOfRelease(1994)
                .rating(9.2)
                .price(120.4)
                .picturePath("shawshank.jpg")
                .build();

        movieResponse2 = MovieResponse.builder()
                .id(2L)
                .nameRussian("Крестный отец")
                .nameNative("The Godfather")
                .yearOfRelease(1972)
                .rating(9.1)
                .price(150.4)
                .picturePath("shawshank.jpg")
                .build();

        fullDtoBase = MovieDetailResponse.builder()
                .id(1L)
                .nameRussian("Побег из Шоушенка")
                .nameNative("The Shawshank Redemption")
                .rating(9.2)
                .price(100.0)
                .picturePath("shawshank.jpg")
                .build();
    }


    @Test
    void findAllShouldUseRepositorySortAndMapToDto() {
        when(movieRepository.findAll(any(Sort.class))).thenReturn(List.of(movie1, movie2));
        when(movieMapper.toResponse(movie1)).thenReturn(movieResponse1);
        when(movieMapper.toResponse(movie2)).thenReturn(movieResponse2);

        List<MovieResponse> result = movieService.findAll(SortDirection.DESC, null);

        assertThat(result).containsExactly(movieResponse1, movieResponse2);
        verify(movieRepository).findAll(any(Sort.class));
        verify(movieMapper).toResponse(movie1);
        verify(movieMapper).toResponse(movie2);
        verifyNoMoreInteractions(movieCacheService, movieEnrichmentService, rateCacheService);
    }

    @Test
    void findAllShouldReturnEmptyWhenRepoReturnsEmpty() {
        when(movieRepository.findAll(any(Sort.class))).thenReturn(List.of());

        List<MovieResponse> result = movieService.findAll(SortDirection.DESC, null);

        assertThat(result).isEmpty();
        verify(movieRepository).findAll(any(Sort.class));
        verifyNoInteractions(movieMapper);
    }


    @Test
    void random_shouldReturnEmptyWhenNoMovies() {
        when(movieRepository.findAll()).thenReturn(List.of());

        List<MovieResponse> result = movieService.random(5);

        assertThat(result).isEmpty();
        verify(movieRepository).findAll();
        verifyNoInteractions(movieMapper);
    }


    @Test
    void findByIdShouldThrowWhenMovieNotInCache() {
        when(movieCacheService.getMovie(1L)).thenReturn(null);

        assertThatThrownBy(() -> movieService.findById(1L, Currency.UAH))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movie not found with id: 1");

        verify(movieCacheService).getMovie(1L);
        verifyNoMoreInteractions(movieCacheService);
        verifyNoInteractions(movieEnrichmentService, rateCacheService);
    }

    @Test
    void findByIdShouldEnrichAndNotConvertPriceForUAH() {
        when(movieCacheService.getMovie(1L)).thenReturn(movie1);
        when(movieEnrichmentService.enrich(movie1)).thenReturn(fullDtoBase);

        MovieDetailResponse result = movieService.findById(1L, Currency.UAH);

        assertThat(result).isSameAs(fullDtoBase);
        assertThat(result.getPrice()).isEqualTo(100.0);

        verify(movieCacheService).getMovie(1L);
        verify(movieEnrichmentService).enrich(movie1);
        verifyNoInteractions(rateCacheService);
    }

    @Test
    void findByIdShouldConvertPriceForUSDWhenRateExists() {
        when(movieCacheService.getMovie(1L)).thenReturn(movie1);
        when(movieEnrichmentService.enrich(movie1)).thenReturn(fullDtoBase);
        when(rateCacheService.findAll()).thenReturn(List.of(
                new RateDTO(1L, "US Dollar", "USD", 50.0),
                new RateDTO(2L, "Euro", "EUR", 42.0)
        ));

        MovieDetailResponse result = movieService.findById(1L, Currency.USD);

        assertThat(result.getPrice()).isEqualTo(100.0 * 50.0);
        verify(rateCacheService).findAll();
    }

    @Test
    void findByIdShouldNotChangePriceWhenRateNotFound() {
        when(movieCacheService.getMovie(1L)).thenReturn(movie1);
        when(movieEnrichmentService.enrich(movie1)).thenReturn(fullDtoBase);
        when(rateCacheService.findAll()).thenReturn(List.of(
                new RateDTO(2L, "Euro", "EUR", 42.0)
        ));

        MovieDetailResponse result = movieService.findById(1L, Currency.USD);

        assertThat(result.getPrice()).isEqualTo(100.0);
        verify(rateCacheService).findAll();
    }


    @Test
    void createShouldMapApplyRelationsUpsertPosterSaveAndUpdateCache() {
        CreateMovieRequest request = new CreateMovieRequest();
        request.setPicturePath("poster.jpg");
        request.setGenres(List.of(2L));
        request.setCountries(List.of(1L));

        Movie mapped = new Movie();
        mapped.setPoster(null);

        Genre genre = new Genre();
        genre.setId(2L);
        Country country = new Country();
        country.setId(1L);

        when(movieMapper.toEntity(request)).thenReturn(mapped);
        when(genreRepository.findAllById(List.of(2L))).thenReturn(List.of(genre));
        when(countryRepository.findAllById(List.of(1L))).thenReturn(List.of(country));
        when(movieRepository.save(mapped)).thenReturn(mapped);

        MovieDetailResponse out = MovieDetailResponse.builder().id(99L).price(10.0).build();
        when(movieMapper.toDetailResponse(mapped)).thenReturn(out);

        MovieDetailResponse result = movieService.create(request);

        assertThat(result).isSameAs(out);

        ArgumentCaptor<Poster> posterCaptor = ArgumentCaptor.forClass(Poster.class);
        verify(posterRepository).save(posterCaptor.capture());
        assertThat(posterCaptor.getValue().getPicturePath()).isEqualTo("poster.jpg");
        assertThat(posterCaptor.getValue().getMovie()).isSameAs(mapped);

        verify(movieRepository).save(mapped);
        verify(movieCacheService).updateMovie(mapped);
        verify(movieMapper).toDetailResponse(mapped);
    }

    @Test
    void createShouldThrowWhenSomeGenresMissing() {
        CreateMovieRequest request = new CreateMovieRequest();
        request.setGenres(List.of(1L, 2L));
        request.setCountries(List.of());
        request.setPicturePath(null);

        when(movieMapper.toEntity(request)).thenReturn(new Movie());
        when(genreRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(new Genre()));

        assertThatThrownBy(() -> movieService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Some genres not found");

        verify(movieRepository, never()).save(any());
        verify(movieCacheService, never()).updateMovie(any());
        verifyNoInteractions(posterRepository);
    }

    @Test
    void createShouldThrowWhenSomeCountriesMissing() {
        CreateMovieRequest request = new CreateMovieRequest();
        request.setGenres(List.of());
        request.setCountries(List.of(10L, 11L));
        request.setPicturePath(null);

        when(movieMapper.toEntity(request)).thenReturn(new Movie());
        when(countryRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(new Country())); // вернули 1

        assertThatThrownBy(() -> movieService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Some countries not found");

        verify(movieRepository, never()).save(any());
        verify(movieCacheService, never()).updateMovie(any());
        verifyNoInteractions(posterRepository);
    }


    @Test
    void updateShouldLoadMovieSaveAndUpdateCache() {
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
        when(genreRepository.findAllById(List.of(4L))).thenReturn(List.of(genre));
        when(countryRepository.findAllById(List.of(3L))).thenReturn(List.of(country));
        when(movieRepository.save(existing)).thenReturn(existing);

        MovieDetailResponse out = MovieDetailResponse.builder().id(1L).build();
        when(movieMapper.toDetailResponse(existing)).thenReturn(out);

        MovieDetailResponse result = movieService.update(1L, request);

        assertThat(result).isSameAs(out);

        verify(movieMapper).updateMovieFromRequest(request, existing);

        verify(posterRepository).save(argThat(p ->
                "new.jpg".equals(p.getPicturePath()) && p.getMovie() == existing
        ));

        verify(movieRepository).save(existing);
        verify(movieCacheService).updateMovie(existing);
        verify(movieMapper).toDetailResponse(existing);
    }

    @Test
    void updateShouldThrowWhenMovieNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.update(1L, new UpdateMovieRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movie not found with id: 1");

        verify(movieRepository).findById(1L);
        verifyNoMoreInteractions(movieRepository);
    }
}