package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.enums.Currency;
import org.dimchik.service.CurrencyService;
import org.dimchik.service.cache.RateCacheService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final RateCacheService rateCacheService;

    @Override
    public void convertPriceInMovieDetailResponse(MovieDetailResponse movieDetailResponse, Currency currency) {
        rateCacheService.findAll().stream()
                .filter(rate -> currency.name().equals(rate.getCurrency()))
                .findFirst()
                .ifPresent(rate -> {
                    BigDecimal rateMultiplier = BigDecimal.valueOf(rate.getRate());
                    BigDecimal price = BigDecimal.valueOf(movieDetailResponse.getPrice());
                    BigDecimal finalPrice = price
                            .multiply(rateMultiplier)
                            .setScale(2, RoundingMode.HALF_UP);

                    movieDetailResponse.setPrice(finalPrice.doubleValue());
                });
    }
}
