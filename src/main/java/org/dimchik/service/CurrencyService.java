package org.dimchik.service;

import org.dimchik.dto.response.MovieDetailResponse;
import org.dimchik.enums.Currency;

public interface CurrencyService {
    void convertPriceInMovieDetailResponse(MovieDetailResponse movieDetailResponse, Currency currency);
}
