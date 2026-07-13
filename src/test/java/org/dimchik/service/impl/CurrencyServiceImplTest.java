package org.dimchik.service.impl;

import org.dimchik.dto.RateDTO;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.enums.Currency;
import org.dimchik.service.cache.RateCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private RateCacheService rateCacheService;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private MovieDetailResponse movieDetail;

    @BeforeEach
    void setUp() {
        movieDetail = MovieDetailResponse.builder()
                .id(1L)
                .nameRussian("Тестовый фильм")
                .price(100.0)
                .build();
    }

    @Test
    void convertPriceShouldMultiplyByUSDrate() {
        when(rateCacheService.findAll()).thenReturn(List.of(
                new RateDTO(1L, "Долар США", "USD", 41.5),
                new RateDTO(2L, "Євро", "EUR", 45.0)
        ));

        currencyService.convertPriceInMovieDetailResponse(movieDetail, Currency.USD);

        assertThat(movieDetail.getPrice()).isEqualTo(100.0 * 41.5);
        verify(rateCacheService).findAll();
    }

    @Test
    void convertPriceShouldMultiplyByEURrate() {
        when(rateCacheService.findAll()).thenReturn(List.of(
                new RateDTO(1L, "Долар США", "USD", 41.5),
                new RateDTO(2L, "Євро", "EUR", 45.0)
        ));

        currencyService.convertPriceInMovieDetailResponse(movieDetail, Currency.EUR);

        assertThat(movieDetail.getPrice()).isEqualTo(100.0 * 45.0);
    }

    @Test
    void convertPriceShouldNotChangePriceForUAH() {
        when(rateCacheService.findAll()).thenReturn(List.of(
                new RateDTO(1L, "Долар США", "USD", 41.5)
        ));

        currencyService.convertPriceInMovieDetailResponse(movieDetail, Currency.UAH);

        assertThat(movieDetail.getPrice()).isEqualTo(100.0);
    }

    @Test
    void convertPriceShouldNotChangePriceWhenRateNotFound() {
        when(rateCacheService.findAll()).thenReturn(List.of(
                new RateDTO(1L, "Євро", "EUR", 45.0)
        ));

        currencyService.convertPriceInMovieDetailResponse(movieDetail, Currency.USD);

        assertThat(movieDetail.getPrice()).isEqualTo(100.0);
    }

    @Test
    void convertPriceShouldNotChangePriceWhenRatesEmpty() {
        when(rateCacheService.findAll()).thenReturn(List.of());

        currencyService.convertPriceInMovieDetailResponse(movieDetail, Currency.USD);

        assertThat(movieDetail.getPrice()).isEqualTo(100.0);
    }
}
