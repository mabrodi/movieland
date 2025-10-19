package org.dimchik.repository.mapper;

import org.dimchik.dto.*;
import org.dimchik.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieRowMapperTest {
    @Mock
    Movie movie;
    @Mock
    Poster poster;

    private final MovieRowMapper mapper = new MovieRowMapper();

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

        MovieDTO movieResponseDTO = mapper.convertToDTO(movie);
        assertEquals(1L, movieResponseDTO.getId());
        assertEquals("Побег из Шоушенка", movieResponseDTO.getNameRussian());
        assertEquals("The Shawshank Redemption", movieResponseDTO.getNameNative());
        assertEquals(1994, movieResponseDTO.getYearOfRelease());
        assertEquals(9.2, movieResponseDTO.getRating());
        assertEquals(120.2, movieResponseDTO.getPrice());
        assertEquals("http://google.com", movieResponseDTO.getPicturePath());
    }

    @Test
    void mapRowShouldReturnMovieFull() {
        Poster poster = new Poster();
        poster.setPicturePath("https://images-na.ssl-images-amazon.com/images/M/MV5BODU4...jpg");

        Country usa = new Country();
        usa.setId(1L);
        usa.setName("США");

        Genre drama = new Genre();
        drama.setId(1L);
        drama.setName("драма");

        Genre crime = new Genre();
        crime.setId(2L);
        crime.setName("криминал");

        User author1 = new User();
        author1.setId(2L);
        author1.setName("Дарлин Эдвардс");

        User author2 = new User();
        author2.setId(3L);
        author2.setName("Габриэль Джексон");

        Review review1 = new Review();
        review1.setId(1L);
        review1.setComment("Гениальное кино!");
        review1.setAuthor(author1);

        Review review2 = new Review();
        review2.setId(2L);
        review2.setComment("Кино это является, безусловно, «со знаком качества».");
        review2.setAuthor(author2);

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setNameRussian("Побег из Шоушенка");
        movie.setNameNative("The Shawshank Redemption");
        movie.setYearOfRelease(1994);
        movie.setRating(8.9);
        movie.setPrice(123.45);
        movie.setPoster(poster);
        movie.setCountries(List.of(usa));
        movie.setGenres(List.of(drama, crime));
        movie.setReviews(List.of(review1, review2));


        MovieFullDTO dto = mapper.convertToFullDTO(movie);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNameRussian()).isEqualTo("Побег из Шоушенка");
        assertThat(dto.getNameNative()).isEqualTo("The Shawshank Redemption");
        assertThat(dto.getYearOfRelease()).isEqualTo(1994);
        assertThat(dto.getRating()).isEqualTo(8.9);
        assertThat(dto.getPrice()).isEqualTo(123.45);
        assertThat(dto.getPicturePath()).contains("images-na.ssl-images");



        assertThat(dto.getCountries())
                .extracting(CountryDTO::getName)
                .containsExactly("США");

        assertThat(dto.getGenres())
                .extracting(GenreDTO::getName)
                .containsExactlyInAnyOrder("драма", "криминал");

        assertThat(dto.getReviews())
                .hasSize(2)
                .extracting(ReviewDTO::getComment)
                .anyMatch(comment -> comment.contains("Гениальное кино!"));

        assertThat(dto.getReviews())
                .extracting(r -> r.getUser().getName())
                .containsExactlyInAnyOrder("Дарлин Эдвардс", "Габриэль Джексон");
    }
}