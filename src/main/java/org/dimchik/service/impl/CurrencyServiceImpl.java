package org.dimchik.service.impl;

import lombok.AllArgsConstructor;
import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.enums.Currency;
import org.dimchik.service.CurrencyService;
import org.dimchik.service.cache.RateCacheService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    private final RateCacheService rateCacheService;

    @Override
    public void convertPriceInMovieDetailResponse(MovieDetailResponse movieDetailResponse, Currency currency) {
        rateCacheService.findAll().stream()
                .filter(rate -> currency.name().equals(rate.getCurrency()))
                .findFirst()
                .ifPresent(rate -> movieDetailResponse.setPrice(movieDetailResponse.getPrice() * rate.getRate()));
    }
}
