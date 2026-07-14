package org.dimchik.service.impl;

import org.dimchik.entity.Country;
import org.dimchik.entity.Movie;
import org.dimchik.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
    }

    @Test
    void enrichSingleMovieByCountriesShouldSetCountriesOnMovie() {
        Country country1 = new Country();
        country1.setId(1L);
        country1.setName("США");
        Country country2 = new Country();
        country2.setId(2L);
        country2.setName("Великобритания");

        when(countryRepository.findAllByMovieId(1L)).thenReturn(List.of(country1, country2));

        countryService.enrichSingleMovieByCountries(movie);

        assertThat(movie.getCountries()).hasSize(2).containsExactly(country1, country2);
        verify(countryRepository).findAllByMovieId(1L);
    }

    @Test
    void enrichSingleMovieByCountriesShouldNotSetWhenThreadInterrupted() {
        Thread.currentThread().interrupt();

        try {
            countryService.enrichSingleMovieByCountries(movie);
            assertThat(movie.getCountries()).isNull();
        } finally {
            Thread.interrupted();
        }

        verify(countryRepository).findAllByMovieId(1L);
    }

    @Test
    void findAllIdsShouldGetCountries() {
        Country country1 = new Country();
        country1.setId(1L);
        Country country2 = new Country();
        country2.setId(2L);

        when(countryRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(country1, country2));

        List<Country> countries = countryService.findAllIds(List.of(1L, 2L));
        assertThat(countries).hasSize(2).containsExactly(country1, country2);
        verify(countryRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    void findAllByIdsShouldDoNothingWhenIdsNull() {
        List<Country> countries = countryService.findAllIds(null);
        assertThat(countries).isEmpty();
    }

    @Test
    void findAllByIdsShouldDoNothingWhenIdsEmpty() {
        List<Country> countries = countryService.findAllIds(List.of());
        assertThat(countries).isEmpty();
    }

    @Test
    void findAllByIdsShouldThrowWhenCountriesNotFound() {
        when(countryRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(new Country()));

        assertThatThrownBy(() -> countryService.findAllIds(List.of(1L, 2L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Some countries not found");

        verify(countryRepository).findAllById(List.of(1L, 2L));
    }
}
