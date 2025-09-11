package org.dimchik.mapper;

import org.dimchik.dto.MovieResponseDTO;
import org.dimchik.entity.Movie;
import org.dimchik.entity.Poster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieRowMapperTest {
    @Mock
    Movie movie;
    @Mock
    Poster poster;

    private MovieRowMapper movieRowMapper;

    @BeforeEach
    void setUp() {
        movieRowMapper = new MovieRowMapper();
    }

    @Test
    void mapRowShouldReturnMovie() {
        when(movie.getId()).thenReturn(1L);
        when(movie.getNameRussian()).thenReturn("Побег из Шоушенка");
        when(movie.getNameNative()).thenReturn("The Shawshank Redemption");
        when(movie.getYearOfRelease()).thenReturn(1994);
        when(movie.getRating()).thenReturn(9.2);
        when(movie.getPrice()).thenReturn(120.2);
        when(movie.getPoster()).thenReturn(poster);
        when(movie.getPoster().getPicturePath()).thenReturn("http://google.com");

        MovieResponseDTO movieResponseDTO = movieRowMapper.convertToDTO(movie);
        assertEquals(1L, movieResponseDTO.getId());
        assertEquals("Побег из Шоушенка", movieResponseDTO.getNameRussian());
        assertEquals("The Shawshank Redemption", movieResponseDTO.getNameNative());
        assertEquals(1994, movieResponseDTO.getYearOfRelease());
        assertEquals(9.2, movieResponseDTO.getRating());
        assertEquals(120.2, movieResponseDTO.getPrice());
        assertEquals("http://google.com", movieResponseDTO.getPicturePath());
    }
}