package org.dimchik.service.base;

import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.dimchik.exception.ResourceNotFoundException;
import org.dimchik.mapper.MovieRowMapper;
import org.dimchik.repository.MovieRepository;
import org.dimchik.repository.specification.MovieSortSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceBaseTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieRowMapper movieRowMapper;

    @InjectMocks
    private MovieServiceBase movieService;

    private Movie shawshank;
    private Movie godfather;
    private Movie darkKnight;
    private MovieResponseDTO shawshankDTO;
    private MovieResponseDTO godfatherDTO;
    private MovieResponseDTO darkKnightDTO;

    @BeforeEach
    void setUp() {
        // Setup test data с реальными данными как в вашем тесте маппера
        shawshank = createMovie(1L, "Побег из Шоушенка", "The Shawshank Redemption", 1994, 9.2, 120.2, "shawshank.jpg");
        godfather = createMovie(2L, "Крестный отец", "The Godfather", 1972, 9.1, 150.0, "godfather.jpg");
        darkKnight = createMovie(3L, "Темный рыцарь", "The Dark Knight", 2008, 9.0, 130.5, "darkknight.jpg");

        shawshankDTO = createMovieDTO(1L, "Побег из Шоушенка", "The Shawshank Redemption", 1994, 9.2, 120.2, "shawshank.jpg");
        godfatherDTO = createMovieDTO(2L, "Крестный отец", "The Godfather", 1972, 9.1, 150.0, "godfather.jpg");
        darkKnightDTO = createMovieDTO(3L, "Темный рыцарь", "The Dark Knight", 2008, 9.0, 130.5, "darkknight.jpg");
    }

    private Movie createMovie(Long id, String nameRussian, String nameNative, int year, double rating, double price, String picturePath) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setNameRussian(nameRussian);
        movie.setNameNative(nameNative);
        movie.setYearOfRelease(year);
        movie.setRating(rating);
        movie.setPrice(price);

        Poster poster = new Poster();
        poster.setPicturePath(picturePath);
        movie.setPoster(poster);

        return movie;
    }

    private MovieResponseDTO createMovieDTO(Long id, String nameRussian, String nameNative, int year, double rating, double price, String picturePath) {
        return MovieResponseDTO.builder()
                .id(id)
                .nameRussian(nameRussian)
                .nameNative(nameNative)
                .yearOfRelease(year)
                .rating(rating)
                .price(price)
                .picturePath(picturePath)
                .build();
    }

    @Test
    void findAllEmptyFilterShouldReturnsMoviesWithDefaultSort() {
        HashMap<String, String> emptyFilter = new HashMap<>();
        List<Movie> movies = Arrays.asList(shawshank, godfather, darkKnight);

        when(movieRepository.findAll(any(Sort.class))).thenReturn(movies);
        when(movieRowMapper.convertToDTO(shawshank)).thenReturn(shawshankDTO);
        when(movieRowMapper.convertToDTO(godfather)).thenReturn(godfatherDTO);
        when(movieRowMapper.convertToDTO(darkKnight)).thenReturn(darkKnightDTO);

        List<MovieResponseDTO> result = movieService.findAll(emptyFilter);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(shawshankDTO, godfatherDTO, darkKnightDTO);
        verify(movieRowMapper, times(3)).convertToDTO(any(Movie.class));
    }

    @Test
    void findAllRatingFilterDescAppliesRatingSortDesc() {
        HashMap<String, String> filter = new HashMap<>();
        filter.put("rating", "desc");
        List<Movie> movies = Arrays.asList(shawshank, godfather, darkKnight);

        when(movieRepository.findAll(any(Sort.class))).thenReturn(movies);
        when(movieRowMapper.convertToDTO(any(Movie.class)))
                .thenReturn(shawshankDTO)
                .thenReturn(godfatherDTO)
                .thenReturn(darkKnightDTO);

        List<MovieResponseDTO> result = movieService.findAll(filter);

        assertThat(result).hasSize(3);
        verify(movieRepository).findAll(MovieSortSpecification.sortByRating("desc"));
    }

    @Test
    void findAllPriceFilterAscAppliesPriceSortAsc() {
        HashMap<String, String> filter = new HashMap<>();
        filter.put("price", "asc");
        List<Movie> movies = Arrays.asList(shawshank, darkKnight, godfather);

        when(movieRepository.findAll(any(Sort.class))).thenReturn(movies);
        when(movieRowMapper.convertToDTO(any(Movie.class)))
                .thenReturn(shawshankDTO)
                .thenReturn(darkKnightDTO)
                .thenReturn(godfatherDTO);

        List<MovieResponseDTO> result = movieService.findAll(filter);

        assertThat(result).hasSize(3);
        verify(movieRepository).findAll(MovieSortSpecification.sortByPrice("asc"));
    }

    @Test
    void randomValidCountShouldReturnsRandomMovies() {
        int count = 2;
        List<Movie> allMovies = Arrays.asList(shawshank, godfather, darkKnight);

        when(movieRepository.findAll()).thenReturn(allMovies);

        List<MovieResponseDTO> result = movieService.random(count);

        assertThat(result).hasSize(count);
        verify(movieRepository).findAll();
        verify(movieRowMapper, times(count)).convertToDTO(any(Movie.class));
    }

    @Test
    void findByIdShouldReturnsMovie() {
        long movieId = 1L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(shawshank));
        when(movieRowMapper.convertToDTO(shawshank)).thenReturn(shawshankDTO);

        MovieResponseDTO result = movieService.findById(movieId);

        assertThat(result).isEqualTo(shawshankDTO);
        assertEquals("Побег из Шоушенка", result.getNameRussian());
        assertEquals("The Shawshank Redemption", result.getNameNative());
        assertEquals(1994, result.getYearOfRelease());
        assertEquals(9.2, result.getRating());
        assertEquals(120.2, result.getPrice());
        assertEquals("shawshank.jpg", result.getPicturePath());

        verify(movieRepository).findById(movieId);
        verify(movieRowMapper).convertToDTO(shawshank);
    }

    @Test
    void findByIdThrowsResourceNotFoundException() {
        long movieId = 999L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.findById(movieId));

        assertThat(exception.getMessage()).contains("Movie not found with id: " + movieId);
        verify(movieRepository).findById(movieId);
        verify(movieRowMapper, never()).convertToDTO(any(Movie.class));
    }

    @Test
    void findByGenreIdShouldReturnsMovies() {
        long genreId = 1L;
        List<Movie> movies = Arrays.asList(shawshank, godfather);

        when(movieRepository.findMoviesByGenreId(genreId)).thenReturn(movies);
        when(movieRowMapper.convertToDTO(shawshank)).thenReturn(shawshankDTO);
        when(movieRowMapper.convertToDTO(godfather)).thenReturn(godfatherDTO);

        List<MovieResponseDTO> result = movieService.findByGenreId(genreId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(shawshankDTO, godfatherDTO);
        verify(movieRepository).findMoviesByGenreId(genreId);
        verify(movieRowMapper, times(2)).convertToDTO(any(Movie.class));
    }

    @Test
    void findAllNoMoviesShouldReturnsEmptyList() {
        HashMap<String, String> filter = new HashMap<>();
        when(movieRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        List<MovieResponseDTO> result = movieService.findAll(filter);

        assertThat(result).isEmpty();
        verify(movieRowMapper, never()).convertToDTO(any(Movie.class));
    }

    @Test
    void randomNoMoviesShouldReturnsEmptyList() {
        int count = 3;
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        List<MovieResponseDTO> result = movieService.random(count);

        assertThat(result).isEmpty();
        verify(movieRowMapper, never()).convertToDTO(any(Movie.class));
    }

    @Test
    void findByGenreIdNoMoviesForGenreShouldReturnsEmptyList() {
        long genreId = 999L;
        when(movieRepository.findMoviesByGenreId(genreId)).thenReturn(Collections.emptyList());

        List<MovieResponseDTO> result = movieService.findByGenreId(genreId);

        assertThat(result).isEmpty();
        verify(movieRepository).findMoviesByGenreId(genreId);
        verify(movieRowMapper, never()).convertToDTO(any(Movie.class));
    }

    @Test
    void randomCountShouldReturnsAllMovies() {
        int count = 5;
        List<Movie> movies = Arrays.asList(shawshank, godfather);

        when(movieRepository.findAll()).thenReturn(movies);
        when(movieRowMapper.convertToDTO(shawshank)).thenReturn(shawshankDTO);
        when(movieRowMapper.convertToDTO(godfather)).thenReturn(godfatherDTO);

        List<MovieResponseDTO> result = movieService.random(count);

        assertThat(result).hasSize(2);
        verify(movieRowMapper, times(2)).convertToDTO(any(Movie.class));
    }

    @Test
    void findAllMultipleFiltersCombinesSorts() {
        HashMap<String, String> filter = new HashMap<>();
        filter.put("rating", "desc");
        filter.put("price", "asc");

        List<Movie> movies = Arrays.asList(shawshank, godfather, darkKnight);
        when(movieRepository.findAll(any(Sort.class))).thenReturn(movies);
        when(movieRowMapper.convertToDTO(any(Movie.class)))
                .thenReturn(shawshankDTO)
                .thenReturn(godfatherDTO)
                .thenReturn(darkKnightDTO);

        List<MovieResponseDTO> result = movieService.findAll(filter);

        assertThat(result).hasSize(3);
        verify(movieRepository).findAll(any(Sort.class));
    }
}