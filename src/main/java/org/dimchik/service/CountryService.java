package org.dimchik.service;

import org.dimchik.entity.Movie;

import java.util.List;

public interface CountryService {
    void enrichSingleMovieByCountries(Movie movie);

    void enrichMovieByCountryIds(Movie movie, List<Long> countryIds);
}
