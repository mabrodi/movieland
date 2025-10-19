package org.dimchik.service.base;

import org.dimchik.common.request.CreateMovieRequest;
import org.dimchik.common.request.MovieByIdRequest;
import org.dimchik.common.request.MovieRequest;
import org.dimchik.common.request.UpdateMovieRequest;
import org.dimchik.dto.MovieDTO;
import org.dimchik.dto.MovieFullDTO;
import org.dimchik.dto.RateDTO;
import org.dimchik.entity.Country;
import org.dimchik.entity.Genre;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.dimchik.repository.CountryRepository;
import org.dimchik.repository.GenreRepository;
import org.dimchik.repository.PosterRepository;
import org.dimchik.service.cache.RateCacheService;
import org.dimchik.web.exception.ResourceNotFoundException;
import org.dimchik.repository.mapper.MovieRowMapper;
import org.dimchik.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceBaseTest {

    @Mock private MovieRepository movieRepository;
    @Mock private GenreRepository genreRepository;
    @Mock private CountryRepository countryRepository;
    @Mock private PosterRepository posterRepository;
    @Mock private MovieRowMapper movieRowMapper;
    @Mock private RateCacheService rateCacheService;

    @InjectMocks
    private MovieServiceBase movieService;

    private Movie movie1;
    private Movie movie2;

    private MovieDTO movieDTO1;
    private MovieDTO movieDTO2;
    private MovieFullDTO movieFullDTO;

    @BeforeEach
    void setUp() {
        Poster poster = new Poster();
        poster.setPicturePath("shawshank.jpg");

        movie1 = Movie.builder().id(1L)
                .nameRussian("Побег из Шоушенка")
                .nameNative("The Shawshank Redemption")
                .yearOfRelease(1994)
                .rating(9.2)
                .price(120.4)
                .poster(poster)
                .build();

        movie2 = Movie.builder().id(2L)
                .nameRussian("Крестный отец")
                .nameNative("The Godfather")
                .yearOfRelease(1972)
                .rating(9.1)
                .price(150.4)
                .poster(poster)
                .build();

        movieDTO1 = MovieDTO.builder()
                .id(1L)
                .nameRussian("Побег из Шоушенка")
                .nameNative("The Shawshank Redemption")
                .yearOfRelease(1994)
                .rating(9.2)
                .price(120.4)
                .picturePath("shawshank.jpg")
                .build();

        movieDTO2 = MovieDTO.builder()
                .id(2L)
                .nameRussian("Крестный отец")
                .nameNative("The Godfather")
                .yearOfRelease(1972)
                .rating(9.1)
                .price(150.4)
                .picturePath("shawshank.jpg")
                .build();

        movieFullDTO = MovieFullDTO.builder()
                .id(1L)
                .nameRussian("Побег из Шоушенка")
                .nameNative("The Shawshank Redemption")
                .rating(9.2)
                .price(100.0)
                .picturePath("shawshank.jpg")
                .build();
    }

    @Test
    void findAllEmptyFilterShouldReturnMoviesWithDefaultSort() {

        List<Movie> movies = Arrays.asList(movie1, movie2);
        when(movieRepository.findAll(any(Sort.class))).thenReturn(movies);

        when(movieRowMapper.convertToDTO(movie1)).thenReturn(movieDTO1);
        when(movieRowMapper.convertToDTO(movie2)).thenReturn(movieDTO2);

        List<MovieDTO> result = movieService.findAll(new MovieRequest());

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(movieDTO1, movieDTO2);
    }


    @Test
    void randomShouldReturnLimitedListWhenMoviesExist() {
        when(movieRepository.findAll()).thenReturn(Arrays.asList(movie1, movie2));
        when(movieRowMapper.convertToDTO(any(Movie.class)))
                .thenReturn(movieDTO1, movieDTO2);

        List<MovieDTO> result = movieService.random(1);

        assertThat(result).hasSize(1);
        verify(movieRepository).findAll();
    }

    @Test
    void randomShouldReturnEmptyListWhenRepositoryEmpty() {
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        List<MovieDTO> result = movieService.random(2);

        assertThat(result).isEmpty();
        verify(movieRepository).findAll();
    }

    @Test
    void findByIdShouldReturnMovieFullDTOWhenCurrencyIsUAH() {
        MovieByIdRequest request = new MovieByIdRequest();
        request.setCurrency(org.dimchik.common.enums.Currency.UAH);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieRowMapper.convertToFullDTO(movie1)).thenReturn(movieFullDTO);

        MovieFullDTO result = movieService.findById(1L, request);

        assertThat(result.getPrice()).isEqualTo(100.0);
        verify(rateCacheService, never()).findAll();
    }

    @Test
    void findByIdShouldConvertPriceWhenCurrencyIsUSD() {
        MovieByIdRequest request = new MovieByIdRequest();
        request.setCurrency(org.dimchik.common.enums.Currency.USD);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieRowMapper.convertToFullDTO(movie1)).thenReturn(movieFullDTO);
        when(rateCacheService.findAll()).thenReturn(List.of(new RateDTO(1L, "Долар США", "USD", 50)));

        MovieFullDTO result = movieService.findById(1L, request);

        assertThat(result.getPrice()).isEqualTo(5000);
        verify(rateCacheService).findAll();
    }

    @Test
    void findByIdShouldThrowExceptionWhenMovieNotFound() {
        MovieByIdRequest request = new MovieByIdRequest();
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.findById(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movie not found with id: 1");
    }

    @Test
    void findByGenreIdShouldReturnMappedDTOs() {
        when(movieRepository.findMoviesByGenreId(10L))
                .thenReturn(Arrays.asList(movie1, movie2));
        when(movieRowMapper.convertToDTO(movie1)).thenReturn(movieDTO1);
        when(movieRowMapper.convertToDTO(movie2)).thenReturn(movieDTO2);

        List<MovieDTO> result = movieService.findByGenreId(10L);

        assertThat(result).containsExactly(movieDTO1, movieDTO2);
        verify(movieRepository).findMoviesByGenreId(10L);
    }

    @Test
    void findByGenreIdShouldReturnEmptyListWhenNoMovies() {
        when(movieRepository.findMoviesByGenreId(anyLong())).thenReturn(Collections.emptyList());

        List<MovieDTO> result = movieService.findByGenreId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void createShouldSaveMovieWithPosterAndRelations() {

        CreateMovieRequest request = new CreateMovieRequest();
        request.setNameRussian("Побег из Шоушенка");
        request.setNameNative("The Shawshank Redemption");
        request.setYearOfRelease(1994);
        request.setDescription("Фильм о надежде и свободе");
        request.setPrice(100.0);
        request.setRating(9.3);
        request.setPicturePath("poster.jpg");
        request.setCountries(List.of(1L));
        request.setGenres(List.of(2L));

        Genre genre = new Genre();
        genre.setId(2L);
        Country country = new Country();
        country.setId(1L);

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setNameRussian("Побег из Шоушенка");

        when(movieRowMapper.convertCreateMovieRequestToMovie(any(CreateMovieRequest.class))).thenReturn(movie);
        when(genreRepository.findById(2L)).thenReturn(Optional.of(genre));
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieRowMapper.convertToFullDTO(any(Movie.class))).thenReturn(movieFullDTO);

        MovieFullDTO result = movieService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        verify(genreRepository).findById(2L);
        verify(countryRepository).findById(1L);
        verify(movieRepository).save(any(Movie.class));
        verify(posterRepository).save(any(Poster.class));
    }

    @Test
    void updateShouldModifyExistingMovie() {

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setNameRussian("Обновленное название");
        request.setPicturePath("updatedPoster.jpg");
        request.setCountries(List.of(3L));
        request.setGenres(List.of(4L));

        Genre genre = new Genre();
        genre.setId(4L);
        Country country = new Country();
        country.setId(3L);

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setNameRussian("Побег из Шоушенка");

        Poster poster = new Poster();
        poster.setPicturePath("oldPoster.jpg");
        movie.setPoster(poster);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(genreRepository.findById(4L)).thenReturn(Optional.of(genre));
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieRowMapper.convertToFullDTO(any(Movie.class))).thenReturn(movieFullDTO);

        MovieFullDTO result = movieService.update(1L, request);

        assertThat(result.getId()).isEqualTo(1L);
        verify(posterRepository).save(any(Poster.class));
        verify(movieRepository).save(movie);
    }
}