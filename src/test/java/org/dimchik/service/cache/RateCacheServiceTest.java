package org.dimchik.service.cache;

import org.dimchik.client.NbuRateClient;
import org.dimchik.dto.RateDTO;
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
class RateCacheServiceTest {

    @Mock
    private NbuRateClient rateClient;

    @InjectMocks
    private RateCacheService rateCacheService;

    @BeforeEach
    void setUp() {
        List<RateDTO> initialRates = List.of(
                new RateDTO(1L, "Долар США", "USD", 41.5),
                new RateDTO(2L, "Євро", "EUR", 45.0)
        );
        when(rateClient.findAll()).thenReturn(initialRates);
        rateCacheService.update();
    }

    @Test
    void updateShouldStoreRatesFromClient() {
        verify(rateClient).findAll();

        List<RateDTO> rates = rateCacheService.findAll();
        assertThat(rates).hasSize(2);
    }

    @Test
    void findAllShouldReturnUnmodifiableList() {
        List<RateDTO> rates = rateCacheService.findAll();

        assertThatThrownBy(() -> rates.add(new RateDTO(3L, "Фунт", "GBP", 52.0)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void updateShouldKeepPreviousRatesWhenClientReturnsEmpty() {
        when(rateClient.findAll()).thenReturn(List.of());

        rateCacheService.update();

        List<RateDTO> rates = rateCacheService.findAll();
        assertThat(rates).hasSize(2);
        assertThat(rates.get(0).getCurrency()).isEqualTo("USD");
    }

    @Test
    void updateShouldReplaceRatesWhenClientReturnsNewData() {
        List<RateDTO> newRates = List.of(
                new RateDTO(3L, "Фунт", "GBP", 52.0)
        );
        when(rateClient.findAll()).thenReturn(newRates);

        rateCacheService.update();

        List<RateDTO> rates = rateCacheService.findAll();
        assertThat(rates).hasSize(1);
        assertThat(rates.get(0).getCurrency()).isEqualTo("GBP");
    }

    @Test
    void updateShouldHandleClientException() {
        when(rateClient.findAll()).thenThrow(new RuntimeException("Connection failed"));

        rateCacheService.update();

        List<RateDTO> rates = rateCacheService.findAll();
        assertThat(rates).hasSize(2);
        assertThat(rates.get(0).getCurrency()).isEqualTo("USD");
    }
}
