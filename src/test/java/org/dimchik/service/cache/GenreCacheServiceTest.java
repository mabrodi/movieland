package org.dimchik.service.cache;

import org.dimchik.dto.GenreDTO;
import org.dimchik.entity.Genre;
import org.dimchik.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreCacheServiceTest {
    @Mock
    private GenreRepository genreRepository;

    private GenreCacheService genreCacheService;

    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    void setUp() {
        genreCacheService = new GenreCacheService(genreRepository);

        genre1 = new Genre(1L, "Drama");
        genre2 = new Genre(2L, "Comedy");
    }

    @Test
    void updateShouldRefreshCacheWhenRepositoryReturnsGenres() throws Exception {
        when(genreRepository.findAll()).thenReturn(List.of(genre1, genre2));

        Method updateMethod = GenreCacheService.class.getDeclaredMethod("update");
        updateMethod.setAccessible(true);
        updateMethod.invoke(genreCacheService);

        List<GenreDTO> result = genreCacheService.findAll();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Drama");
        assertThat(result.get(1).getName()).isEqualTo("Comedy");

        verify(genreRepository, times(1)).findAll();
    }

    @Test
    void findAllShouldReturnCopyOfList() throws Exception {
        when(genreRepository.findAll()).thenReturn(List.of(genre1));
        Method updateMethod = GenreCacheService.class.getDeclaredMethod("update");
        updateMethod.setAccessible(true);
        updateMethod.invoke(genreCacheService);

        List<GenreDTO> first = genreCacheService.findAll();
        List<GenreDTO> second = genreCacheService.findAll();

        assertThat(first).isNotSameAs(second);
        assertThat(first).hasSize(1);
        assertThat(second).hasSize(1);
    }

    @Test
    void updateShouldHandleExceptionGracefully() throws Exception {
        when(genreRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        Method updateMethod = GenreCacheService.class.getDeclaredMethod("update");
        updateMethod.setAccessible(true);

        updateMethod.invoke(genreCacheService);

        verify(genreRepository, times(1)).findAll();
        assertThat(genreCacheService.findAll()).isEmpty();
    }
}