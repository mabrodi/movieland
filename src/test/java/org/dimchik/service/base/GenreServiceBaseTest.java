package org.dimchik.service.base;

import org.dimchik.dto.GenreResponseDTO;
import org.dimchik.entity.Genre;
import org.dimchik.repository.GenreResponse;
import org.dimchik.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceBaseTest {

    private GenreService genreService;

    @Mock
    private GenreResponse genreResponse;

    @BeforeEach
    void setUp() {
        genreService = new GenreServiceBase(genreResponse);
    }

    @Test
    void findAllShouldReturnEmptyListInitially() {
        List<GenreResponseDTO> result = genreService.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void updateShouldPopulateListWithGenres() {
        Genre genre1 = new Genre(1L, "Action");
        Genre genre2 = new Genre(2L, "Comedy");
        List<Genre> genres = Arrays.asList(genre1, genre2);

        when(genreResponse.findAll()).thenReturn(genres);

        GenreServiceBase genreServiceBase = (GenreServiceBase) genreService;
        genreServiceBase.update();

        List<GenreResponseDTO> result = genreService.findAll();

        assertThat(result)
                .hasSize(2)
                .containsExactly(
                        new GenreResponseDTO(1L, "Action"),
                        new GenreResponseDTO(2L, "Comedy")
                );

        verify(genreResponse).findAll();
    }

    @Test
    void updateShouldClearExistingList() {
        Genre genre1 = new Genre(1L, "Action");
        Genre genre2 = new Genre(2L, "Comedy");
        List<Genre> firstBatch = Arrays.asList(genre1, genre2);

        Genre genre3 = new Genre(3L, "Drama");
        List<Genre> secondBatch = List.of(genre3);

        when(genreResponse.findAll())
                .thenReturn(firstBatch)
                .thenReturn(secondBatch);

        GenreServiceBase genreServiceBase = (GenreServiceBase) genreService;
        genreServiceBase.update();

        List<GenreResponseDTO> firstResult = genreService.findAll();
        assertThat(firstResult).hasSize(2);

        genreServiceBase.update();

        List<GenreResponseDTO> secondResult = genreService.findAll();

        assertThat(secondResult)
                .hasSize(1)
                .containsExactly(new GenreResponseDTO(3L, "Drama"));

        verify(genreResponse, times(2)).findAll();
    }

    @Test
    void updateShouldHandleEmptyRepository() {
        when(genreResponse.findAll()).thenReturn(List.of());

        GenreServiceBase genreServiceBase = (GenreServiceBase) genreService;
        genreServiceBase.update();

        List<GenreResponseDTO> result = genreService.findAll();

        assertThat(result).isEmpty();
        verify(genreResponse).findAll();
    }


    @Test
    void constructorShouldInitializeEmptyList() {
        List<GenreResponseDTO> result = genreService.findAll();
        assertThat(result).isEmpty();
    }
}